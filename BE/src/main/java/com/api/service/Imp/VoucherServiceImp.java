package com.api.service.Imp;

import com.api.dto.request.VoucherRequest;
import com.api.dto.response.VoucherResponse;
import com.api.entity.Restaurant;
import com.api.entity.Voucher;
import com.api.entity.VoucherDetail;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.mapper.Imp.VoucherMapperImp;

import com.api.repository.VoucherDetailRepository;
import com.api.repository.VoucherRepository;
import com.api.service.RestaurantService;
import com.api.service.VoucherService;
import com.api.utils.VoucherStatus;
import com.api.utils.VoucherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImp implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final RestaurantService restaurantService;
    private final VoucherDetailRepository voucherDetailRepository;

    @Override
    public VoucherResponse addVoucher(VoucherRequest request) {
        VoucherMapperImp voucherMapper = new VoucherMapperImp();
        Voucher voucher = voucherMapper.toVoucher(request);
        //Check voucher value
        log.info("Check voucher value of voucher {}", voucher.getId());
        checkVoucherValue(request.getType(), request.getValue());
        Restaurant restaurant = new Restaurant();
        if(request.getRestaurant_id() > 0) {
            restaurant = restaurantService.getRestaurant(request.getRestaurant_id());
        } else {
            restaurant = null;
        }
        voucher.setRestaurant(restaurant);
        voucherRepository.save(voucher);
        //
        VoucherResponse response = voucherMapper.toVoucherResponse(voucher);
        if(restaurant != null) {
            response.setRestaurant_name(restaurant.getName());
        }
        return response;
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
            voucher.setQuantity(request.getQuantity());
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
            if (voucherDetailRepository.findByVoucherIdAndEndDateAfter(voucher.getId(), LocalDateTime.now()) != null)
            {
                listVoucherApply.add(voucher);
            }
        }

        return listVoucherApply.stream().map(voucher -> {
            VoucherResponse response = voucherMapper.toVoucherResponse(voucher);
            VoucherDetail detail = voucherDetailRepository.findByVoucherIdAndEndDateAfter(voucher.getId(), LocalDateTime.now());
            response.setEndTime(detail.getEndDate().toString());
            return response;
        }).toList();
    }

    public void checkVoucherValue( VoucherType type,BigDecimal value) {
        if(type.equals(VoucherType.PERCENTAGE)) {
            if (value.compareTo(BigDecimal.ZERO) <= 0 || value.compareTo(new BigDecimal("100")) > 0) {
                throw  new AppException(ErrorCode.VOUCHER_VALUE_CONFLICT);
            }
        }
    }
}
