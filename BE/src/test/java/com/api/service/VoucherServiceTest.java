package com.api.service;

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
import com.api.repository.FoodRepository;
import com.api.repository.VoucherDetailRepository;
import com.api.repository.VoucherRepository;
import com.api.service.Imp.VoucherServiceImp;
import com.api.utils.VoucherApplyType;
import com.api.utils.VoucherStatus;
import com.api.utils.VoucherType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private VoucherRepository voucherRepository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private RestaurantService restaurantService;

    @Mock
    private VoucherDetailRepository voucherDetailRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private VoucherMapperImp voucherMapperImp;

    @InjectMocks
    private VoucherServiceImp voucherService;

    private VoucherRequest voucherRequest;
    private Voucher voucher;
    private Restaurant restaurant;
    private VoucherResponse voucherResponse;

    @BeforeEach
    void setUp() {
        voucherRequest = VoucherRequest.builder()
                .code("TEST001")
                .type(VoucherType.PERCENTAGE)
                .value(new BigDecimal("20"))
                .description("Test voucher")
                .minRequire(new BigDecimal("100"))
                .status(VoucherStatus.ACTIVE)
                .restaurant_id(1L)
                .build();

        restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .build();
        restaurant.setId(1L);

        voucher = Voucher.builder()
                .code(voucherRequest.getCode())
                .description(voucherRequest.getDescription())
                .minRequire(voucherRequest.getMinRequire())
                .type(voucherRequest.getType())
                .applyType(voucherRequest.getApplyType())
                .status(voucherRequest.getStatus())
                .value(voucherRequest.getValue())
                .build();
        voucher.setId(1L);

        voucherResponse = VoucherResponse.builder()
                .code(voucher.getCode())
                .description(voucher.getDescription())
                .id(voucher.getId())
                .minRequire(voucher.getMinRequire())
                .status(voucher.getStatus())
                .applyType(voucher.getApplyType())
                .type(voucher.getType())
                .value(voucher.getValue())
                .build();
    }

    @Test
    void addVoucher_Success() {
        // Given
        when(voucherRepository.existsByCode("TEST001")).thenReturn(false);
        when(voucherMapperImp.toVoucher(any(VoucherRequest.class))).thenReturn(voucher);
        when(voucherRepository.save(any(Voucher.class))).thenAnswer(invocation -> {
            Voucher savedVoucher = invocation.getArgument(0);
            savedVoucher.setId(1L); // giả lập DB set id sau khi save
            return savedVoucher;
        });
        when(voucherMapperImp.toVoucherResponse(voucher)).thenReturn(voucherResponse);
        // When
        VoucherResponse response = voucherService.addVoucher(voucherRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("TEST001", response.getCode());
        verify(voucherRepository).save(any(Voucher.class));
    }

    @Test
    void addVoucher_CodeExists_ThrowsException() {
        // Given
        when(voucherRepository.existsByCode("TEST001")).thenReturn(true);

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> voucherService.addVoucher(voucherRequest));
        assertEquals(ErrorCode.VOUCHER_CODE_EXISTED, exception.getErrorCode());
    }

    @Test
    void addVoucher_InvalidPercentageValue_ThrowsException() {
        // Given
        voucherRequest.setValue(new BigDecimal("150")); // Invalid percentage > 100
        when(voucherRepository.existsByCode("TEST001")).thenReturn(false);

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> voucherService.addVoucher(voucherRequest));
        assertEquals(ErrorCode.VOUCHER_VALUE_CONFLICT, exception.getErrorCode());
    }

    @Test
    void getVoucherById_Success() {
        // Given
        when(voucherRepository.findById(1L)).thenReturn(Optional.of(voucher));

        // When
        Voucher result = voucherService.getVoucherbyId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TEST001", result.getCode());
    }

    @Test
    void getVoucherById_NotFound_ThrowsException() {
        // Given
        when(voucherRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> voucherService.getVoucherbyId(1L));
        assertEquals(ErrorCode.VOUCHER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void deleteVoucher_Success() {
        // Given
        when(voucherDetailRepository.existsByVoucherId(1L)).thenReturn(false);

        // When
        voucherService.deleteVoucher(1L);

        // Then
        verify(voucherRepository).deleteById(1L);
    }

    @Test
    void deleteVoucher_VoucherDetailExists_ThrowsException() {
        // Given
        when(voucherDetailRepository.existsByVoucherId(1L)).thenReturn(true);

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> voucherService.deleteVoucher(1L));
        assertEquals(ErrorCode.VOUCHER_ID_EXISTED, exception.getErrorCode());
    }

    @Test
    void updateVoucher_Success() {
        // Given
        when(voucherRepository.findById(1L)).thenReturn(Optional.of(voucher));
        when(voucherDetailRepository.existsByVoucherId(1L)).thenReturn(false);
        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(voucherRepository.save(any(Voucher.class))).thenReturn(voucher);

        // When
        VoucherResponse response = voucherService.updateVoucher(1L, voucherRequest);

        // Then
        assertNotNull(response);
        assertEquals("TEST001", response.getCode());
        verify(voucherRepository).save(any(Voucher.class));
    }

    @Test
    void updateVoucherStatus_ActiveToInactive() {
        // Given
        voucher.setStatus(VoucherStatus.ACTIVE);
        when(voucherRepository.findById(1L)).thenReturn(Optional.of(voucher));
        when(voucherRepository.save(any(Voucher.class))).thenReturn(voucher);

        // When
        voucherService.updateVoucherStatus(1L);

        // Then
        verify(voucherRepository).save(argThat(v -> v.getStatus() == VoucherStatus.INACTIVE));
    }

    @Test
    void getVoucherCanApply_Success() {
        // Given
        BigDecimal totalPrice = new BigDecimal("200");
        VoucherDetail voucherDetail = VoucherDetail.builder()
                .voucher(voucher)
                .endDate(LocalDateTime.now().plusDays(1))
                .quantity(10)
                .build();

        when(voucherRepository.findByRestaurantIdIsNullAndStatusAndMinRequireLessThanEqual(
                VoucherStatus.ACTIVE, totalPrice)).thenReturn(Arrays.asList(voucher));
        when(voucherDetailRepository.findByVoucherIdAndEndDateAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(voucherDetail);

        // When
        List<VoucherResponse> responses = voucherService.getVoucherCanApply(totalPrice);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("TEST001", responses.get(0).getCode());
    }

    @Test
    void addVoucherRestaurant_Success() {
        // Given
        VoucherRequest request = VoucherRequest.builder()
                .code("REST001")
                .applyType(VoucherApplyType.ALL)
                .description("Restaurant voucher")
                .type(VoucherType.PERCENTAGE)
                .value(new BigDecimal("15"))
                .restaurant_id(1L)
                .startDate(LocalDateTime.now().plusHours(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(voucherRepository.existsByCodeAndRestaurant("REST001", restaurant)).thenReturn(false);
        when(voucherRepository.save(any(Voucher.class))).thenReturn(voucher);
        when(voucherDetailRepository.save(any(VoucherDetail.class))).thenReturn(new VoucherDetail());

        // When
        long result = voucherService.addVoucherRestaurant(request);

        // Then
        assertTrue(result > 0);
        verify(voucherRepository).save(any(Voucher.class));
        verify(voucherDetailRepository).save(any(VoucherDetail.class));
    }

    @Test
    void addVoucherRestaurant_DuplicateCode_ThrowsException() {
        // Given
        VoucherRequest request = VoucherRequest.builder()
                .code("REST001")
                .restaurant_id(1L)
                .startDate(LocalDateTime.now().plusHours(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .description("")
                .build();

        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(voucherRepository.existsByCodeAndRestaurant("REST001", restaurant)).thenReturn(true);

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> voucherService.addVoucherRestaurant(request));
        assertEquals(ErrorCode.VOUCHER_DUPLICATED, exception.getErrorCode());
    }

    @Test
    void addVoucherRestaurant_InvalidTime_ReturnsMinusOne() {
        // Given
        VoucherRequest request = VoucherRequest.builder()
                .code("REST001")
                .restaurant_id(1L)
                .startDate(LocalDateTime.now().minusHours(1)) // Past time
                .endDate(LocalDateTime.now().plusDays(1))
                .description("")
                .build();

        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);

        // When
        long result = voucherService.addVoucherRestaurant(request);

        // Then
        assertEquals(-1, result);
    }

    @Test
    void extendVoucher_Success() {
        // Given
        AddVoucherDetailRequestRes request = AddVoucherDetailRequestRes.builder()
                .voucher_id(1L)
                .startDate(LocalDateTime.now().plusHours(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .foodIds(Arrays.asList(1L, 2L))
                .build();

        Food food1 = new Food();
        food1.setId(1L);
        food1.setName("Food 1");

        Food food2 = new Food();
        food1.setId(2L);
        food1.setName("Food 2");

        voucher.setApplyType(VoucherApplyType.SPECIFIC);
        when(voucherRepository.findById(1L)).thenReturn(Optional.of(voucher));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food1));
        when(foodRepository.findById(2L)).thenReturn(Optional.of(food2));
        when(voucherDetailRepository.save(any(VoucherDetail.class))).thenReturn(new VoucherDetail());
        when(foodRepository.save(any(Food.class))).thenReturn(food1);
        when(voucherRepository.save(any(Voucher.class))).thenReturn(voucher);

        // When
        Boolean result = voucherService.extendVoucher(request);

        // Then
        assertTrue(result);
        verify(voucherDetailRepository, times(2)).save(any(VoucherDetail.class));
    }

    @Test
    void deleteVoucherRestaurant_Success() {
        // Given
        VoucherDetail voucherDetail = VoucherDetail.builder()
                .startDate(LocalDateTime.now().plusHours(1)) // Future start date
                .build();
        voucher.setVoucherDetails(Arrays.asList(voucherDetail));
        voucher.setRestaurant(restaurant);
        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(voucherRepository.findById(1L)).thenReturn(Optional.of(voucher));

        // When
        boolean result = voucherService.deleteVoucherRestaurant(1L, 1L);

        // Then
        assertTrue(result);
        verify(voucherDetailRepository).deleteAll(anyList());
        verify(voucherRepository).delete(voucher);
    }

    @Test
    void deleteVoucherRestaurant_VoucherStarted_ReturnsFalse() {
        // Given
        VoucherDetail voucherDetail = VoucherDetail.builder()
                .startDate(LocalDateTime.now().minusHours(1)) // Past start date
                .build();
        voucher.setVoucherDetails(Arrays.asList(voucherDetail));
        voucher.setRestaurant(restaurant);
        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(voucherRepository.findById(1L)).thenReturn(Optional.of(voucher));

        // When
        boolean result = voucherService.deleteVoucherRestaurant(1L, 1L);

        // Then
        assertFalse(result);
        verify(voucherDetailRepository, never()).deleteAll(anyList());
        verify(voucherRepository, never()).delete(any());
    }
}
