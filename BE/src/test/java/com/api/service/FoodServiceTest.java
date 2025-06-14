package com.api.service;

import com.api.dto.request.AddAdditionalFoodsRequest;
import com.api.dto.request.AddFoodRequest;
import com.api.dto.request.AdjustFoodPriceRequest;
import com.api.dto.request.UpdateFoodInfoRequest;
import com.api.dto.response.*;
import com.api.entity.*;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.FoodMainAndAdditionalRepository;
import com.api.repository.FoodRepository;
import com.api.service.Imp.FoodServiceImp;
import com.api.utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private FoodTypeService foodTypeService;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private FoodMainAndAdditionalRepository foodMainAndAdditionalRepository;

    @Mock
    private VoucherService voucherService;

    @Mock
    private VoucherDetailService voucherDetailService;

    @InjectMocks
    private FoodServiceImp foodService;

    private Restaurant restaurant;
    private FoodType foodType;
    private Food food;
    private FoodDetail foodDetail;
    private Voucher voucher;
    private VoucherDetail voucherDetail;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");
        restaurant.setFoods(new ArrayList<>());

        foodType = new FoodType();
        foodType.setId(1L);
        foodType.setName("Main Course");
        foodType.setFoods(new ArrayList<>());

        foodDetail = new FoodDetail();
        foodDetail.setId(1L);
        foodDetail.setPrice(new BigDecimal("25.99"));
        foodDetail.setStartTime(LocalDateTime.now().minusDays(1));
        foodDetail.setEndTime(null);

        food = new Food();
        food.setId(1L);
        food.setName("Test Food");
        food.setDescription("Test Description");
        food.setKind(FoodKind.MAIN);
        food.setImage("test-image.jpg");
        food.setType(foodType);
        food.setStatus(FoodStatus.ACTIVE);
        food.setRestaurant(restaurant);
        food.setFoodDetails(new ArrayList<>(List.of(foodDetail)));
        food.setMainFoods(new ArrayList<>());
        food.setAdditionFoods(new ArrayList<>());
        food.setVoucherDetails(new ArrayList<>());
        food.setCartDetails(new ArrayList<>());

        foodDetail.setFood(food);

        voucher = new Voucher();
        voucher.setId(1L);
        voucher.setType(VoucherType.PERCENTAGE);
        voucher.setValue(new BigDecimal("10"));
        voucher.setStatus(VoucherStatus.ACTIVE);
        voucher.setApplyType(VoucherApplyType.ALL);
        voucher.setRestaurant(restaurant);
        voucher.setVoucherDetails(new ArrayList<>());

        voucherDetail = new VoucherDetail();
        voucherDetail.setId(1L);
        voucherDetail.setVoucher(voucher);
        voucherDetail.setFood(food);
        voucherDetail.setStartDate(LocalDateTime.now().minusDays(1));
        voucherDetail.setEndDate(LocalDateTime.now().plusDays(1));
    }

    @Test
    void addFood_Success() {
        // Given
        AddFoodRequest request = new AddFoodRequest();
        request.setRestaurantId(1L);
        request.setName("New Food");
        request.setDescription("New Description");
        request.setKind(FoodKind.MAIN);
        request.setImage("new-image.jpg");
        request.setType("Main Course");
        request.setPrice(new BigDecimal("30.00"));

        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodTypeService.getFoodTypeByName("Main Course")).thenReturn(foodType);
        when(foodRepository.existsByRestaurantAndNameAndTypeAndKind(
                restaurant, "New Food", foodType, FoodKind.MAIN)).thenReturn(false);
        when(foodRepository.save(any(Food.class))).thenReturn(food);

        // When
        long result = foodService.addFood(request);

        // Then
        assertEquals(1L, result);
        verify(foodRepository).save(any(Food.class));
    }

    @Test
    void addFood_FoodAlreadyExists_ThrowsException() {
        // Given
        AddFoodRequest request = new AddFoodRequest();
        request.setRestaurantId(1L);
        request.setName("Existing Food");
        request.setType("Main Course");
        request.setKind(FoodKind.MAIN);

        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodTypeService.getFoodTypeByName("Main Course")).thenReturn(foodType);
        when(foodRepository.existsByRestaurantAndNameAndTypeAndKind(
                restaurant, "Existing Food", foodType, FoodKind.MAIN)).thenReturn(true);

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> foodService.addFood(request));
        assertEquals(ErrorCode.FOOD_OF_RETAURANT_EXISTED, exception.getErrorCode());
    }

    @Test
    void adjustFoodPrice_Success() {
        // Given
        AdjustFoodPriceRequest request = new AdjustFoodPriceRequest();
        request.setFoodId(1L);
        request.setRestaurantId(1L);
        request.setOldPrice(new BigDecimal("25.99"));
        request.setNewPrice(new BigDecimal("30.00"));

        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(foodRepository.save(any(Food.class))).thenReturn(food);

        // When
        long result = foodService.adjustFoodPrice(request);

        // Then
        assertEquals(1L, result);
        verify(foodRepository).save(food);
        assertNotNull(foodDetail.getEndTime());
    }

    @Test
    void adjustFoodPrice_SamePrices_ThrowsException() {
        // Given
        AdjustFoodPriceRequest request = new AdjustFoodPriceRequest();
        request.setFoodId(1L);
        request.setRestaurantId(1L);
        request.setOldPrice(new BigDecimal("25.99"));
        request.setNewPrice(new BigDecimal("25.99"));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> foodService.adjustFoodPrice(request));
        assertEquals(ErrorCode.FOOD_PRICE_REDUNDANT, exception.getErrorCode());
    }

    @Test
    void adjustFoodPrice_PriceConflict_ThrowsException() {
        // Given
        AdjustFoodPriceRequest request = new AdjustFoodPriceRequest();
        request.setFoodId(1L);
        request.setRestaurantId(1L);
        request.setOldPrice(new BigDecimal("20.00"));
        request.setNewPrice(new BigDecimal("30.00"));

        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> foodService.adjustFoodPrice(request));
        assertEquals(ErrorCode.FOOD_DETAIL_CONFLICT_PRICE, exception.getErrorCode());
    }

    @Test
    void getCurrentPrice_Success() {
        // Given
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        // When
        BigDecimal result = foodService.getCurrentPrice(1L);

        // Then
        assertEquals(new BigDecimal("25.99"), result);
    }

    @Test
    void getCurrentPrice_NoActivePrice_ReturnsZero() {
        // Given
        foodDetail.setEndTime(LocalDateTime.now().minusHours(1));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        // When
        BigDecimal result = foodService.getCurrentPrice(1L);

        // Then
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void getFoodPriceIn_Success() {
        // Given
        LocalDateTime testTime = LocalDateTime.now();
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(voucherService.getVoucherOfRestaurant(1L)).thenReturn(Collections.emptyList());

        // When
        BigDecimal result = foodService.getFoodPriceIn(1L, testTime);

        // Then
        assertEquals(new BigDecimal("25.99"), result);
    }

    @Test
    void getFoodPriceIn_WithVoucher_AppliesDiscount() {
        // Given
        LocalDateTime testTime = LocalDateTime.now();
        food.getVoucherDetails().add(voucherDetail);
        voucher.getVoucherDetails().add(voucherDetail);

        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(voucherService.getVoucherOfRestaurant(1L)).thenReturn(Collections.emptyList());

        // When
        BigDecimal result = foodService.getFoodPriceIn(1L, testTime);

        // Then
        // 10% discount should be applied: 25.99 - (25.99 * 0.10) = 23.39
        assertEquals(new BigDecimal("23.39"), result);
    }

    @Test
    void getFood_Success_ForCustomer() {
        // Given
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        // When
        GetFoodResponse result = foodService.getFood(1L, true);

        // Then
        assertNotNull(result);
        assertEquals("Test Food", result.getName());
        assertEquals("test-image.jpg", result.getImage());
        assertEquals("Test Description", result.getDescription());
        assertEquals(new BigDecimal("25.99"), result.getPrice());
    }

    @Test
    void getFood_InactiveFood_ForCustomer_ThrowsException() {
        // Given
        food.setStatus(FoodStatus.INACTIVE);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> foodService.getFood(1L, true));
        assertEquals(ErrorCode.FOOD_NOT_PUBLIC_FOR_CUSTOMER, exception.getErrorCode());
    }

    @Test
    void getFood_FoodNotFound_ThrowsException() {
        // Given
        when(foodRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> foodService.getFood(1L, true));
        assertEquals(ErrorCode.FOOD_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getFoodsOfRestaurant_Success() {
        // Given
        restaurant.getFoods().add(food);
        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        // When
        List<GetFoodResponse> result = foodService.getFoodsOfRestaurant(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Food", result.get(0).getName());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void changeFoodStatus_Success() {
        // Given
        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(foodRepository.save(any(Food.class))).thenReturn(food);

        // When
        foodService.changeFoodStatus(1L, 1L, FoodStatus.INACTIVE);

        // Then
        assertEquals(FoodStatus.INACTIVE, food.getStatus());
        verify(foodRepository).save(food);
    }

    @Test
    void getAdditionalFoodsOfRestaurant_Success() {
        // Given
        Food additionalFood = new Food();
        additionalFood.setId(2L);
        additionalFood.setName("Additional Food");
        additionalFood.setKind(FoodKind.ADDITIONAL);
        additionalFood.setStatus(FoodStatus.ACTIVE);
        additionalFood.setFoodDetails(new ArrayList<>(List.of(foodDetail)));
        additionalFood.setRestaurant(restaurant);
        restaurant.getFoods().addAll(List.of(food, additionalFood));

        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            Page<Food> mockPage = new PageImpl<>(List.of(additionalFood));
            pageUtilsMock.when(() -> PageUtils.convertListToPage(anyList(), any(Pageable.class)))
                    .thenReturn(mockPage);

            when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
            when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
            when(voucherService.getVoucherOfRestaurant(1L)).thenReturn(Collections.emptyList());
            when(foodRepository.findById(2L)).thenReturn(Optional.of(additionalFood));

            // When
            PageResponse<List<GetFoodResponse>> result =
                    foodService.getAdditionalFoodsOfRestaurant(1L, true, 0, 10);

            // Then
            assertNotNull(result);
            assertEquals(0, result.getPage());
            assertEquals(10, result.getSize());
            assertEquals(1L, result.getTotal());
        }
    }

    @Test
    void addAdditionalFoodToFoodOfRestaurant_Success() {
        // Given
        Food additionalFood = new Food();
        additionalFood.setId(2L);
        additionalFood.setName("Additional Food");
        additionalFood.setKind(FoodKind.ADDITIONAL);
        additionalFood.setRestaurant(restaurant);
        additionalFood.setAdditionFoods(new ArrayList<>());

        AddAdditionalFoodsRequest request = new AddAdditionalFoodsRequest();
        request.setFoodId(1L);
        request.setRestaurantId(1L);
        request.setAdditionalFoodIds(Set.of(2));

        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(foodRepository.findById(2L)).thenReturn(Optional.of(additionalFood));
        when(foodMainAndAdditionalRepository.existsByMainFoodAndAdditionFood(food, additionalFood))
                .thenReturn(false);
        when(foodMainAndAdditionalRepository.save(any(FoodMainAndAddition.class)))
                .thenReturn(new FoodMainAndAddition());

        // When
        foodService.addAdditionalFoodToFoodOfRestaurant(request);

        // Then
        verify(foodMainAndAdditionalRepository).save(any(FoodMainAndAddition.class));
    }

    @Test
    void addAdditionalFoodToFoodOfRestaurant_FoodIsAdditional_ThrowsException() {
        // Given
        food.setKind(FoodKind.ADDITIONAL);
        AddAdditionalFoodsRequest request = new AddAdditionalFoodsRequest();
        request.setFoodId(1L);
        request.setRestaurantId(1L);
        request.setAdditionalFoodIds(Set.of(2));

        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> foodService.addAdditionalFoodToFoodOfRestaurant(request));
        assertEquals(ErrorCode.FOOD_ADDITIONAL, exception.getErrorCode());
    }

    @Test
    void getAdditionalFoodsOfFood_Success() {
        // Given
        Food additionalFood = new Food();
        additionalFood.setId(2L);
        additionalFood.setName("Additional Food");
        additionalFood.setKind(FoodKind.ADDITIONAL);
        additionalFood.setStatus(FoodStatus.ACTIVE);
        additionalFood.setFoodDetails(new ArrayList<>(List.of(foodDetail)));
        additionalFood.setRestaurant(restaurant);

        FoodMainAndAddition relation = new FoodMainAndAddition();
        relation.setMainFood(food);
        relation.setAdditionFood(additionalFood);

        food.getMainFoods().add(relation);

        try (MockedStatic<PageUtils> pageUtilsMock = mockStatic(PageUtils.class)) {
            Page<Food> mockPage = new PageImpl<>(List.of(additionalFood));
            pageUtilsMock.when(() -> PageUtils.convertListToPage(anyList(), any(Pageable.class)))
                    .thenReturn(mockPage);

            when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
            when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
            when(voucherService.getVoucherOfRestaurant(1L)).thenReturn(Collections.emptyList());
            when(foodRepository.findById(2L)).thenReturn(Optional.of(additionalFood));

            // When
            PageResponse<List<GetFoodResponse>> result =
                    foodService.getAdditionalFoodsOfFood(1L, 1L, true, 0, 10);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getTotal());
        }
    }

    @Test
    void getAdditionalFoodsOfFood_FoodIsAdditional_ThrowsException() {
        // Given
        food.setKind(FoodKind.ADDITIONAL);
        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> foodService.getAdditionalFoodsOfFood(1L, 1L, true, 0, 10));
        assertEquals(ErrorCode.FOOD_ADDITIONAL, exception.getErrorCode());
    }

    @Test
    void updateFoodInfo_Success() {
        // Given
        UpdateFoodInfoRequest request = new UpdateFoodInfoRequest();
        request.setName(Optional.of("Updated Name"));
        request.setImage(Optional.of("updated-image.jpg"));
        request.setDescription(Optional.of("Updated Description"));
        request.setStatus(Optional.of(FoodStatus.INACTIVE));
        request.setFoodKind(Optional.of(FoodKind.ADDITIONAL));
        request.setFoodType(Optional.of("Dessert"));

        FoodType dessertType = new FoodType();
        dessertType.setId(2L);
        dessertType.setName("Dessert");

        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(foodTypeService.getFoodTypeByName("Dessert")).thenReturn(dessertType);
        when(foodRepository.save(any(Food.class))).thenReturn(food);

        // When
        foodService.updateFoodInfo(1L, 1L, request);

        // Then
        assertEquals("Updated Name", food.getName());
        assertEquals("updated-image.jpg", food.getImage());
        assertEquals("Updated Description", food.getDescription());
        assertEquals(FoodStatus.INACTIVE, food.getStatus());
        assertEquals(FoodKind.ADDITIONAL, food.getKind());
        assertEquals(dessertType, food.getType());
        verify(foodRepository).save(food);
    }

    @Test
    void updateFoodInfo_WithPriceChange_Success() {
        // Given
        UpdateFoodInfoRequest request = new UpdateFoodInfoRequest();
        request.setOldPrice(Optional.of(new BigDecimal("25.99")));
        request.setNewPrice(Optional.of(new BigDecimal("30.00")));

        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(foodRepository.save(any(Food.class))).thenReturn(food);

        // When
        foodService.updateFoodInfo(1L, 1L, request);

        // Then
        verify(foodRepository, times(2)).save(food); // Once for price adjustment, once for final save
    }

    @Test
    void deleteFood_Success() {
        // Given
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        // When
        foodService.deleteFood(1L);

        // Then
        verify(foodRepository).delete(food);
    }

    @Test
    void deleteFood_HasOrderedItems_ThrowsException() {
        // Given
        Order order = new Order();
        order.setId(1L);

        CartDetail orderedCartDetail = new CartDetail();
        orderedCartDetail.setId(1L);
        orderedCartDetail.setOrder(order); // Has order

        food.getCartDetails().add(orderedCartDetail);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        // When & Then
        AppException exception = assertThrows(AppException.class,
                () -> foodService.deleteFood(1L));
        assertEquals(ErrorCode.CAN_NOT_DELETE_ORDERED_FOOD, exception.getErrorCode());
    }

    @Test
    void getFoodGroupOfRestaurant_Success() {
        // Given
        restaurant.getFoods().add(food);
        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(voucherService.getVoucherOfRestaurant(1L)).thenReturn(Collections.emptyList());
        when(voucherDetailService.getVoucherDetailByVoucherInAndFoodInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                anyList(), anyList(), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        // When
        GetFoodGroupResponse result = foodService.getFoodGroupOfRestaurant(1L, true);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTypes().size());
        assertTrue(result.getTypes().contains("Main Course"));
        assertEquals(1, result.getFoods().size());
        assertEquals("Test Food", result.getFoods().get(0).getName());
    }

    @Test
    void searchFoods_Success() {
        // Given
        String query = "Test";
        List<Food> searchResults = List.of(food);

        when(foodRepository.findByNameContainingIgnoreCase(query, FoodStatus.ACTIVE, FoodKind.MAIN))
                .thenReturn(searchResults);
        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(voucherService.getVoucherOfRestaurant(1L)).thenReturn(Collections.emptyList());
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        // When
        List<GetFoodResponse> result = foodService.searchFoods(query, null, true);

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Food", result.get(0).getName());
    }

    @Test
    void searchFoods_EmptyQuery_ReturnsEmptyList() {
        // When
        List<GetFoodResponse> result = foodService.searchFoods("", null, true);

        // Then
        assertTrue(result.isEmpty());
    }
}