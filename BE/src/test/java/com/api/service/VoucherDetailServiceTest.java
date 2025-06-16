package com.api.service;


import com.api.dto.request.AddVoucherDetailRequest;
import com.api.dto.response.VoucherDetailResponse;
import com.api.entity.Food;
import com.api.entity.Restaurant;
import com.api.entity.Voucher;
import com.api.entity.VoucherDetail;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.FoodRepository;
import com.api.repository.VoucherDetailRepository;
import com.api.service.Imp.VoucherDetailServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherDetailServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private VoucherService voucherService;

    @Mock
    private VoucherDetailRepository voucherDetailRepository;

    @InjectMocks
    private VoucherDetailServiceImp voucherDetailService;

    private AddVoucherDetailRequest request;
    private Voucher voucher;
    private Food food;
    private Restaurant restaurant;
    private LocalDateTime validStartDate;
    private LocalDateTime validEndDate;

    @BeforeEach
    void setUp() {
        validStartDate = LocalDateTime.now().plusDays(1);
        validEndDate = LocalDateTime.now().plusDays(10);

        request = new AddVoucherDetailRequest();
        request.setQuantity(100);
        request.setStartDate(validStartDate);
        request.setEndDate(validEndDate);
        request.setVoucher_id(1L);
        request.setFood_id(1L);

        restaurant = new Restaurant();
        restaurant.setId(1L);

        food = new Food();
        food.setId(1L);
        restaurant.setFoods(Arrays.asList(food));

        voucher = new Voucher();
        voucher.setId(1L);
        voucher.setRestaurant(restaurant);
    }

    @Test
    void addVoucherDetails_WithValidRequestAndFood_ShouldReturnVoucherDetailResponse() {
        // Given
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(voucherService.getVoucherbyId(1L)).thenReturn(voucher);
        when(voucherDetailRepository.save(any(VoucherDetail.class))).thenAnswer(invocation -> {
            VoucherDetail vd = invocation.getArgument(0);
            vd.setId(1L);
            return vd;
        });

        VoucherDetail existingDetail = new VoucherDetail();
        existingDetail.setFood(food);
        when(voucherDetailRepository.findByStartDateAndEndDateAndVoucherId(
                validStartDate, validEndDate, 1L))
                .thenReturn(Arrays.asList(existingDetail));

        // When
        VoucherDetailResponse result = voucherDetailService.addVoucherDetails(request);

        // Then
        assertNotNull(result);
        assertEquals(100, result.getQuantity());
        assertEquals(validStartDate, result.getStartDate());
        assertEquals(validEndDate, result.getEndDate());
        assertEquals(1L, result.getVoucher_id());
        assertNotNull(result.getFood_ids());
        assertEquals(1, result.getFood_ids().size());
        assertEquals(1L, result.getFood_ids().get(0));

        verify(voucherDetailRepository).save(any(VoucherDetail.class));
    }

    @Test
    void addVoucherDetails_WithValidRequestAndNoFood_ShouldReturnVoucherDetailResponseWithNullFoodIds() {
        // Given
        request.setFood_id(null);
        when(voucherService.getVoucherbyId(1L)).thenReturn(voucher);
        when(voucherDetailRepository.save(any(VoucherDetail.class))).thenAnswer(invocation -> {
            VoucherDetail vd = invocation.getArgument(0);
            vd.setId(1L);
            return vd;
        });

        // When
        VoucherDetailResponse result = voucherDetailService.addVoucherDetails(request);

        // Then
        assertNotNull(result);
        assertEquals(100, result.getQuantity());
        assertNull(result.getFood_ids());
        verify(voucherDetailRepository).save(any(VoucherDetail.class));
    }

    @Test
    void addVoucherDetails_WithFoodNotInRestaurant_ShouldThrowException() {
        // Given
        Food differentFood = new Food();
        differentFood.setId(2L);

        when(foodRepository.findById(1L)).thenReturn(Optional.of(differentFood));
        when(voucherService.getVoucherbyId(1L)).thenReturn(voucher);

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> voucherDetailService.addVoucherDetails(request));
        assertEquals(ErrorCode.FOOD_RESTAURANT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void addVoucherDetails_WithInvalidStartDate_ShouldThrowException() {
        // Given
        request.setStartDate(LocalDateTime.now().plusDays(10));
        request.setEndDate(LocalDateTime.now().plusDays(1)); // End date before start date

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> voucherDetailService.addVoucherDetails(request));
        assertEquals(ErrorCode.INVALID_TIME, exception.getErrorCode());
    }

    @Test
    void addVoucherDetails_WithEndDateInPast_ShouldThrowException() {
        // Given
        request.setStartDate(LocalDateTime.now().minusDays(2));
        request.setEndDate(LocalDateTime.now().minusDays(1)); // End date in past

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> voucherDetailService.addVoucherDetails(request));
        assertEquals(ErrorCode.INVALID_TIME, exception.getErrorCode());
    }

    @Test
    void getVoucherDetailByVoucherInAndFoodInAndStartDateLessThanEqualAndEndDateGreaterThanEqual_ShouldReturnVoucherDetails() {
        // Given
        List<Voucher> voucherList = Arrays.asList(voucher);
        List<Food> foodList = Arrays.asList(food);
        LocalDateTime currentTime = LocalDateTime.now();

        VoucherDetail voucherDetail1 = new VoucherDetail();
        VoucherDetail voucherDetail2 = new VoucherDetail();
        List<VoucherDetail> expectedDetails = Arrays.asList(voucherDetail1, voucherDetail2);

        when(voucherDetailRepository.findByVoucherInAndFoodInAndStartDateBeforeAndEndDateAfter(
                voucherList, foodList, currentTime, currentTime))
                .thenReturn(expectedDetails);

        // When
        List<VoucherDetail> result = voucherDetailService
                .getVoucherDetailByVoucherInAndFoodInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        voucherList, foodList, currentTime);

        // Then
        assertEquals(expectedDetails, result);
        verify(voucherDetailRepository).findByVoucherInAndFoodInAndStartDateBeforeAndEndDateAfter(
                voucherList, foodList, currentTime, currentTime);
    }
}
