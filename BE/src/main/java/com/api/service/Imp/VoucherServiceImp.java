package com.api.service.Imp;

import com.api.dto.request.AddVoucherDetailRequestRes;
import com.api.dto.request.VoucherRequest;
import com.api.dto.response.VoucherResponse;
import com.api.entity.Food;
import com.api.entity.Restaurant;
import com.api.entity.Voucher;
import com.api.entity.VoucherDetail;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.mapper.Imp.VoucherMapperImp;

import com.api.mapper.VoucherMapper;
import com.api.repository.FoodRepository;
import com.api.repository.VoucherDetailRepository;
import com.api.repository.VoucherRepository;
import com.api.service.FoodService;
import com.api.service.RestaurantService;
import com.api.service.VoucherService;
import com.api.utils.VoucherApplyType;
import com.api.utils.VoucherStatus;
import com.api.utils.VoucherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImp implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final RestaurantService restaurantService;
    private final VoucherDetailRepository voucherDetailRepository;
    private final FoodRepository foodRepository;

    @Override
    public VoucherResponse addVoucher(VoucherRequest request) {
        VoucherMapperImp voucherMapper = new VoucherMapperImp();
        Voucher voucher = voucherMapper.toVoucher(request);
        //Check voucher value
        log.info("Check voucher value of voucher {}", voucher.getId());
        checkVoucherValue(request.getType(), request.getValue());
        if (voucherRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.VOUCHER_CODE_EXISTED);
        }
        voucher.setRestaurant(null);
        voucherRepository.save(voucher);
        //

        return voucherMapper.toVoucherResponse(voucher);
    }

    @Override
    public Voucher getVoucherbyId(long id) {
        return voucherRepository.findById(id).orElseThrow(() -> {
            log.info("Voucher not found");
            return new AppException(ErrorCode.VOUCHER_NOT_FOUND);
        });
    }

    @Override
    public void deleteVoucher(long voucher_id) {
        boolean check = voucherDetailRepository.existsByVoucherId(voucher_id);
        if(check) {
            throw new AppException(ErrorCode.VOUCHER_ID_EXISTED);
        } else {
            voucherRepository.deleteById(voucher_id);
        }
    }

    @Override
    public VoucherResponse updateVoucher(long id, VoucherRequest request) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        boolean check = voucherDetailRepository.existsByVoucherId(id);
        if(check) {
            throw new AppException(ErrorCode.VOUCHER_ID_EXISTED);
        } else {
            voucher.setCode(request.getCode());
            voucher.setStatus(request.getStatus());
            voucher.setType(request.getType());
            voucher.setValue(request.getValue());
            voucher.setDescription(request.getDescription());
            voucher.setValue(request.getValue());
            voucher.setMinRequire(request.getMinRequire());

            checkVoucherValue(request.getType(), request.getValue());

            Restaurant restaurant = null;
            if(request.getRestaurant_id() > 0) {
                restaurant = restaurantService.getRestaurant(request.getRestaurant_id());
                voucher.setRestaurant(restaurant);
            } else {
                voucher.setRestaurant(null);
            }

            Voucher savedVoucher = voucherRepository.save(voucher);
            VoucherMapperImp voucherMapper = new VoucherMapperImp();
            VoucherResponse response = voucherMapper.toVoucherResponse(savedVoucher);
            if(restaurant != null) {
                response.setRestaurant_name(restaurant.getName());
            }
            return response;
        }
    }

    @Override
    public List<VoucherResponse> getAllVoucher() {
        VoucherMapperImp voucherMapper = new VoucherMapperImp();
        return voucherRepository.findAll().stream().map(voucher -> {
            VoucherResponse response = null;
            response = voucherMapper.toVoucherResponse(voucher);
            if(voucher.getRestaurant() != null) {
                response.setRestaurant_name(voucher.getRestaurant().getName());
            }
            return response;
        }).toList();
    }

    @Override
    public Voucher findVoucherByCode(String code) {
        return voucherRepository.findByCode(code).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
    }

    @Override
    public List<VoucherResponse> getVoucherCanApply(BigDecimal totalPrice) {
        VoucherMapperImp voucherMapper = new VoucherMapperImp();
        List<Voucher> listVoucherApply = new ArrayList<>();
        List<Voucher> voucherList = voucherRepository.findByRestaurantIdIsNullAndStatusAndMinRequireLessThanEqual(VoucherStatus.ACTIVE, totalPrice);
        for (Voucher voucher : voucherList) {
            VoucherDetail detail = voucherDetailRepository.findByVoucherIdAndEndDateAfter(voucher.getId(), LocalDateTime.now());

            if (detail != null && detail.getQuantity() > 0)
            {
                listVoucherApply.add(voucher);
            }
        }

        return listVoucherApply.stream().map(voucher -> {
            VoucherResponse response = voucherMapper.toVoucherResponse(voucher);
            VoucherDetail detail = voucherDetailRepository.findByVoucherIdAndEndDateAfter(voucher.getId(), LocalDateTime.now());
            response.setEndTime(detail.getEndDate());
            return response;
        }).toList();
    }

    @Override
    public List<Voucher> getVoucherOfRestaurant(long restaurantId) {
        log.info("Get voucher of restaurant {}", restaurantId);
        return voucherRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public List<VoucherResponse> getRestaurantVoucher(long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);
        LocalDateTime now = LocalDateTime.now();

        return restaurant.getVouchers().stream()
                .map(voucher -> {
                    List<VoucherDetail> sortedDetails = voucher.getVoucherDetails().stream()
                            .sorted(Comparator.comparing(VoucherDetail::getStartDate).reversed()) // gần nhất trước
                            .collect(Collectors.toList());

                    List<VoucherDetail> validDetails = sortedDetails.stream()
                            .filter(detail -> detail.getEndDate().isAfter(now)) // còn hiệu lực
                            .collect(Collectors.toList());

                    if (validDetails.isEmpty()) {
                        // Không còn hiệu lực -> lấy nhóm cuối cùng để trả foodIds (nếu có)
                        if (!sortedDetails.isEmpty()) {
                            VoucherDetail selectedDetail = sortedDetails.get(0);
                            List<Long> foodIds = voucher.getApplyType() == VoucherApplyType.SPECIFIC
                                    ? sortedDetails.stream()
                                    .filter(d -> d.getStartDate().equals(selectedDetail.getStartDate()) &&
                                            d.getEndDate().equals(selectedDetail.getEndDate()))
                                    .map(d -> d.getFood().getId())
                                    .collect(Collectors.toList())
                                    : Collections.emptyList();

                            return buildVoucherResponse(voucher, selectedDetail.getStartDate(), selectedDetail.getEndDate(), foodIds);
                        } else {
                            return buildVoucherResponse(voucher, null, null, Collections.emptyList());
                        }
                    }

                    VoucherDetail selectedDetail = validDetails.get(0);

                    List<Long> foodIds = voucher.getApplyType() == VoucherApplyType.SPECIFIC
                            ? validDetails.stream()
                            .filter(d -> d.getStartDate().equals(selectedDetail.getStartDate()) &&
                                    d.getEndDate().equals(selectedDetail.getEndDate()))
                            .map(d -> d.getFood().getId())
                            .collect(Collectors.toList())
                            : Collections.emptyList();

                    return buildVoucherResponse(
                            voucher,
                            selectedDetail.getStartDate(),
                            selectedDetail.getEndDate(),
                            foodIds
                    );
                })
                .collect(Collectors.toList());
    }


    private VoucherResponse buildVoucherResponse(Voucher voucher, LocalDateTime start, LocalDateTime end, List<Long> foodIds) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .description(voucher.getDescription())
                .value(voucher.getValue())
                .type(voucher.getType())
                .applyType(voucher.getApplyType())
                .status(voucher.getStatus())
                .startTime(start)
                .endTime(end)
                .foodIds(foodIds)
                .isActive(end.isAfter(LocalDateTime.now()))
                .build();
    }

    @Override
    public void updateVoucherStatus(long voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        if (voucher.getStatus().equals(VoucherStatus.ACTIVE)) {
            voucher.setStatus(VoucherStatus.INACTIVE);
        } else {
            voucher.setStatus(VoucherStatus.ACTIVE);
        }
        log.info("Update voucher status of voucher {} to {}", voucherId, voucher.getStatus());
        voucherRepository.save(voucher);
    }

    private void checkValidTime(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            log.error("Start time must before or equal end time");
            throw new AppException(ErrorCode.INVALID_TIME);
        }
    }

    @Override
    @Transactional
    public long addVoucherRestaurant(VoucherRequest request) {
        checkValidTime(request.getStartDate(), request.getEndDate());
        if (!LocalDateTime.now().isBefore(request.getStartDate().plusMinutes(1))) { // add 1 min is request is long and now() can in-consistence
            return -1;
        }
        Restaurant restaurant = restaurantService.getRestaurant(request.getRestaurant_id());

        if (voucherRepository.existsByCodeAndRestaurant(request.getCode(), restaurant)) {
            log.error("Voucher code {} already exists in restaurant {}", request.getCode(), request.getRestaurant_id());
            throw new AppException(ErrorCode.VOUCHER_DUPLICATED);
        }

        // 1. Tạo và lưu voucher trước
        Voucher voucher = Voucher.builder()
                .code(request.getCode())
                .applyType(request.getApplyType())
                .description(request.getDescription())
                .restaurant(restaurant)
                .type(request.getType())
                .value(request.getValue())
                .status(VoucherStatus.ACTIVE)
                .build();
        voucher = voucherRepository.save(voucher); // save first!

        // 2. Sau đó tạo voucherDetails và gán voucher đã có ID
        if (request.getApplyType().equals(VoucherApplyType.ALL)) {
            VoucherDetail voucherDetail = VoucherDetail.builder()
                    .voucher(voucher)
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .quantity(-1)
                    .build();
            voucherDetailRepository.save(voucherDetail);
        } else if (request.getApplyType().equals(VoucherApplyType.SPECIFIC)) {
            Voucher finalVoucher = voucher;
            request.getFoodIds().stream()
                    .map(id -> foodRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .forEach(f -> {
                        VoucherDetail voucherDetail = VoucherDetail.builder()
                                .voucher(finalVoucher)
                                .food(f)
                                .startDate(request.getStartDate())
                                .endDate(request.getEndDate())
                                .quantity(-1)
                                .build();
                        voucherDetailRepository.save(voucherDetail);

                        // optional: update food entity if needed
                        f.getVoucherDetails().add(voucherDetail);
                        foodRepository.save(f);
                    });
        }

        return voucher.getId();
    }

    @Override
    @Transactional
    public Boolean extendVoucher(AddVoucherDetailRequestRes request) {
        Voucher voucher = voucherRepository.findById(request.getVoucher_id())
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        checkValidTime(request.getStartDate(), request.getEndDate());
        if (!LocalDateTime.now().isBefore(request.getStartDate().plusMinutes(1))) { // add 1 min is request is long and now() can in-consistence
            return false;
        }

        if (voucher.getApplyType().equals(VoucherApplyType.ALL)) {
            VoucherDetail vd = VoucherDetail.builder()
                    .voucher(voucher)
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .quantity(-1)
                    .build();
            voucherDetailRepository.save(vd);
        }
        request.getFoodIds().stream()
                .forEach(id -> foodRepository.findById(id)
                        .ifPresent(f -> {
                            VoucherDetail vd = VoucherDetail.builder()
                                    .voucher(voucher)
                                    .food(f)
                                    .startDate(request.getStartDate())
                                    .endDate(request.getEndDate())
                                    .quantity(-1)
                                    .build();
                            voucherDetailRepository.save(vd);
                            foodRepository.save(f);
                        }));
        voucherRepository.save(voucher);
        return true;
    }

    @Override
    public List<VoucherResponse> getAdminVoucher() {
        VoucherMapper voucherMapper = new VoucherMapperImp();
        List<VoucherResponse> responses = voucherRepository.findByRestaurantIsNull().stream().map(voucher -> {
            VoucherResponse response = voucherMapper.toVoucherResponse(voucher);
            boolean check = checkActiveVoucher(voucher.getId());
            response.setActive(check);
            if (check) {
                VoucherDetail detail = voucherDetailRepository.findByVoucherIdAndEndDateAfter(voucher.getId(), LocalDateTime.now());
                response.setStartTime(detail.getStartDate());
                response.setEndTime(detail.getEndDate());
            }
            return response;
        } ).toList();
        return responses;
    }

    @Override
    @Transactional
    public boolean deleteVoucherRestaurant(long restaurantId, long voucherId) {
        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);
        Voucher restaurantVoucher = voucherRepository.findById(voucherId).orElse(null);
        if (restaurantVoucher == null) {
            return false;
        }
        if (!restaurantVoucher.getRestaurant().equals(restaurant)) {
            return false;
        }
        var check = restaurantVoucher.getVoucherDetails().stream()
                .filter(vd -> vd.getStartDate().isBefore(LocalDateTime.now()))
                .findAny();
        if (check.isPresent()) {
            return false;
        }
        voucherDetailRepository.deleteAll(restaurantVoucher.getVoucherDetails());
        voucherRepository.delete(restaurantVoucher);
        return true;
    }


    public void checkVoucherValue( VoucherType type,BigDecimal value) {
        if(type.equals(VoucherType.PERCENTAGE)) {
            if (value.compareTo(BigDecimal.ZERO) <= 0 || value.compareTo(new BigDecimal("100")) > 0) {
                throw  new AppException(ErrorCode.VOUCHER_VALUE_CONFLICT);
            }
        }
    }

    private boolean checkActiveVoucher(long voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow(() ->
                new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        List<VoucherDetail> voucherDetailList = voucher.getVoucherDetails();
        for (VoucherDetail voucherDetail: voucherDetailList) {
            if (voucherDetail.getEndDate().isAfter(LocalDateTime.now())) {
                return true;
            }
        }
        return false;
    }
}
