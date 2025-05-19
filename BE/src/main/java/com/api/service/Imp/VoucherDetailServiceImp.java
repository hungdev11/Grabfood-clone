package com.api.service.Imp;

import com.api.dto.request.AddVoucherDetailRequest;
import com.api.dto.response.VoucherDetailResponse;
import com.api.entity.Food;
import com.api.entity.Voucher;
import com.api.entity.VoucherDetail;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.FoodRepository;
import com.api.repository.VoucherDetailRepository;
import com.api.service.VoucherDetailService;
import com.api.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoucherDetailServiceImp implements VoucherDetailService {
    private final FoodRepository foodRepository;
    private final VoucherService voucherService;
    private final VoucherDetailRepository voucherDetailRepository;
    @Override
    public VoucherDetailResponse addVoucherDetails(AddVoucherDetailRequest request) {
        checkStartDateAndEndDate(request.getStartDate(), request.getEndDate());
        VoucherDetail voucherDetail = VoucherDetail.builder()
                .quantity(request.getQuantity())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        Food food = null;
        if (request.getFood_id() != null) {
            food = foodRepository.findById(request.getFood_id()).orElse(null);
        }
        Voucher voucher = voucherService.getVoucherbyId(request.getVoucher_id());

        if(voucher.getRestaurant()!=null && request.getFood_id()!=null) {
            if (voucher.getRestaurant().getFoods().contains(food)) {
                voucherDetail.setFood(food);
            } else {
                throw new AppException(ErrorCode.FOOD_RESTAURANT_NOT_FOUND);
            }
        } else {
            voucherDetail.setFood(null);
        }
        voucherDetail.setVoucher(voucher);
        voucherDetailRepository.save(voucherDetail);
        VoucherDetailResponse response = VoucherDetailResponse.builder()
                .quantity(voucherDetail.getQuantity())
                .startDate(voucherDetail.getStartDate())
                .endDate(voucherDetail.getEndDate())
                .voucher_id(voucherDetail.getVoucher().getId())
                .build();
        if (voucherDetail.getFood() == null) {
            response.setFood_ids(null);
        } else {
            List<VoucherDetail> list = voucherDetailRepository.findByStartDateAndEndDateAndVoucherId(voucherDetail.getStartDate(), voucherDetail.getEndDate(), voucherDetail.getVoucher().getId());
            List<Long> listResponse = new ArrayList<>();
            for (VoucherDetail voucherDetail1: list) {
                listResponse.add(voucherDetail1.getFood().getId());
            }
            response.setFood_ids(listResponse);
        }
        return response;
    }

    @Override
    public List<VoucherDetail> getVoucherDetailByVoucherInAndFoodInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(List<Voucher> voucherList, List<Food> foodList, LocalDateTime currentTime) {
        return voucherDetailRepository.findByVoucherInAndFoodInAndStartDateBeforeAndEndDateAfter(voucherList, foodList, currentTime, currentTime);
    }

    private void checkStartDateAndEndDate(LocalDateTime startDate, LocalDateTime endDate)
    {
        if (startDate.isAfter(endDate) || LocalDateTime.now().isAfter(endDate)) {
            throw new AppException(ErrorCode.INVALID_TIME);
        }
    }
}
