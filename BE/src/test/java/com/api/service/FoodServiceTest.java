package com.api.service;

import com.api.dto.request.AddFoodRequest;
import com.api.dto.request.AdjustFoodPriceRequest;
import com.api.entity.Food;
import com.api.entity.FoodDetail;
import com.api.entity.FoodType;
import com.api.entity.Restaurant;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.FoodMainAndAdditionalRepository;
import com.api.repository.FoodRepository;
import com.api.service.Imp.FoodServiceImp;
import com.api.utils.FoodKind;
import com.api.utils.FoodStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @InjectMocks
    private FoodServiceImp foodServiceImp;

    private AddFoodRequest request;
    private Restaurant mockRestaurant;
    private FoodType mockFoodType;

    @BeforeEach
    public void setupAddFood() {
        request = AddFoodRequest.builder()
                .type("Pizza")
                .name("Cheese Pizza")
                .price(BigDecimal.valueOf(10))
                .description("Very good")
                .kind(FoodKind.MAIN)
                .image("a.jpg")
                .restaurantId(1L)
                .build();

        mockRestaurant = new Restaurant();
        mockRestaurant.setId(1L);
        mockRestaurant.setFoods(new ArrayList<>());

        mockFoodType = new FoodType();
        mockFoodType.setName("Pizza");
        mockFoodType.setFoods(new ArrayList<>());
    }

    private final long foodId = 1L;
    private final long restaurantId = 1L;
    private final BigDecimal oldPrice = new BigDecimal("10.00");
    private final BigDecimal newPrice = new BigDecimal("12.00");

    private AdjustFoodPriceRequest createValidRequest() {
        return AdjustFoodPriceRequest.builder()
                .foodId(foodId)
                .restaurantId(restaurantId)
                .oldPrice(oldPrice)
                .newPrice(newPrice)
                .build();
    }

    private Restaurant createRestaurant() {
        Restaurant r = new Restaurant();
        r.setId(restaurantId);
        return r;
    }

    private Food createFoodWithDetail(BigDecimal price) {
        Food food = new Food();
        food.setId(foodId);
        food.setRestaurant(createRestaurant());

        FoodDetail detail = new FoodDetail();
        detail.setFood(food);
        detail.setPrice(price);
        detail.setStartTime(LocalDateTime.now().minusDays(1));

        List<FoodDetail> details = new ArrayList<>();
        details.add(detail);

        food.setFoodDetails(details);
        return food;
    }


    @Test
    void givenAddFoodRequest_whenAddFood_thenReturnFoodId() {
        // Given
        given(restaurantService.getRestaurant(request.getRestaurantId())).willReturn(mockRestaurant);
        given(foodTypeService.getFoodTypeByName(request.getType())).willReturn(mockFoodType);
        given(foodRepository.existsByRestaurantAndNameAndTypeAndKind(
                any(), anyString(), any(), any())).willReturn(false);

        Food savedFood = Food.builder()
                .kind(FoodKind.MAIN)
                .name(request.getName())
                .kind(request.getKind())
                .type(mockFoodType)
                .restaurant(mockRestaurant)
                .status(FoodStatus.ACTIVE)
                .foodDetails(new ArrayList<>())
                .build();
        savedFood.setId(1L);

        given(foodRepository.save(any(Food.class))).willReturn(savedFood);

        // When
        long result = foodServiceImp.addFood(request);

        // Then
        assertEquals(1L, result);
        verify(foodRepository, times(1)).save(any(Food.class));
    }

    @Test
    public void givenExistedFood_whenAddFood_thenThrowException() {
        given(restaurantService.getRestaurant(request.getRestaurantId())).willReturn(mockRestaurant);
        given(foodTypeService.getFoodTypeByName(request.getType())).willReturn(mockFoodType);
        given(foodRepository.existsByRestaurantAndNameAndTypeAndKind(
                any(), anyString(), any(), any()
        )).willReturn(true);

        AppException ex = assertThrows(AppException.class, () -> {
            foodServiceImp.addFood(request);
        });

        assertEquals(ErrorCode.FOOD_OF_RETAURANT_EXISTED, ex.getErrorCode());
        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    public void givenNotExistedRestaurant_whenAddFood_thenThrowException() {
        given(restaurantService.getRestaurant(anyLong()))
                .willThrow(new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        AppException ex = assertThrows(AppException.class, () -> {
            foodServiceImp.addFood(request);
        });

        assertEquals(ErrorCode.RESTAURANT_NOT_FOUND, ex.getErrorCode());
        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    public void givenNotExistedFoodType_whenAddFood_thenThrowException() {
        given(restaurantService.getRestaurant(request.getRestaurantId())).willReturn(mockRestaurant);
        given(foodTypeService.getFoodTypeByName(anyString()))
                .willThrow(new AppException(ErrorCode.FOODTYPE_NAME_NOT_EXISTED));

        AppException ex = assertThrows(AppException.class, () -> {
            foodServiceImp.addFood(request);
        });

        assertEquals(ErrorCode.FOODTYPE_NAME_NOT_EXISTED, ex.getErrorCode());
        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    void adjustFoodPrice_success() {
        AdjustFoodPriceRequest request = createValidRequest();
        Food food = createFoodWithDetail(oldPrice);

        given(restaurantService.getRestaurant(restaurantId)).willReturn(createRestaurant());
        given(foodRepository.findById(foodId)).willReturn(Optional.of(food));
        given(foodRepository.save(any())).willReturn(food);

        long result = foodServiceImp.adjustFoodPrice(request);

        assertEquals(foodId, result);
        assertEquals(2, food.getFoodDetails().size()); // Có thêm 1 detail mới
    }

    @Test
    void adjustFoodPrice_throw_whenPricesAreTheSame() {
        AdjustFoodPriceRequest request = createValidRequest();
        request.setNewPrice(oldPrice); // same

        AppException ex = assertThrows(AppException.class, () ->
                foodServiceImp.adjustFoodPrice(request)
        );

        assertEquals(ErrorCode.FOOD_PRICE_REDUNDANT, ex.getErrorCode());
    }

    @Test
    void adjustFoodPrice_throw_whenFoodNotBelongToRestaurant() {
        AdjustFoodPriceRequest request = createValidRequest();

        // Giả lập food thuộc nhà hàng khác
        Restaurant another = Restaurant.builder().build();
        another.setId(999L);
        Food food = createFoodWithDetail(oldPrice);
        food.setRestaurant(another);

        given(restaurantService.getRestaurant(restaurantId)).willReturn(createRestaurant());
        given(foodRepository.findById(foodId)).willReturn(Optional.of(food));

        AppException ex = assertThrows(AppException.class, () ->
                foodServiceImp.adjustFoodPrice(request)
        );

        assertEquals(ErrorCode.FOOD_RESTAURANT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void adjustFoodPrice_throw_whenNoFoodDetails() {
        AdjustFoodPriceRequest request = createValidRequest();
        Food food = Food.builder()
                .restaurant(createRestaurant())
                .foodDetails(new ArrayList<>()) // empty list
                .build();
        food.setId(foodId);

        given(restaurantService.getRestaurant(restaurantId)).willReturn(createRestaurant());
        given(foodRepository.findById(foodId)).willReturn(Optional.of(food));

        AppException ex = assertThrows(AppException.class, () ->
                foodServiceImp.adjustFoodPrice(request)
        );

        assertEquals(ErrorCode.FOOD_RESTAURANT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void adjustFoodPrice_throw_whenOldPriceConflict() {
        AdjustFoodPriceRequest request = createValidRequest();
        Food food = createFoodWithDetail(new BigDecimal("9.00")); // khác oldPrice

        given(restaurantService.getRestaurant(restaurantId)).willReturn(createRestaurant());
        given(foodRepository.findById(foodId)).willReturn(Optional.of(food));

        AppException ex = assertThrows(AppException.class, () ->
                foodServiceImp.adjustFoodPrice(request)
        );

        assertEquals(ErrorCode.FOOD_DETAIL_CONFLICT_PRICE, ex.getErrorCode());
    }

    @Test
    void adjustFoodPrice_throw_whenRestaurantNotFound() {
        AdjustFoodPriceRequest request = createValidRequest();

        given(restaurantService.getRestaurant(restaurantId))
                .willThrow(new AppException(ErrorCode.RESTAURANT_NOT_FOUND));

        AppException ex = assertThrows(AppException.class, () ->
                foodServiceImp.adjustFoodPrice(request)
        );

        assertEquals(ErrorCode.RESTAURANT_NOT_FOUND, ex.getErrorCode());
    }




}

