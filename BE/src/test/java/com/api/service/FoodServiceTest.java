package com.api.service;

import com.api.dto.request.AddAdditionalFoodsRequest;
import com.api.dto.request.AddFoodRequest;
import com.api.dto.request.AdjustFoodPriceRequest;
import com.api.dto.response.GetFoodResponse;
import com.api.dto.response.PageResponse;
import com.api.entity.*;
import com.api.exception.AppException;
import com.api.repository.FoodMainAndAdditionalRepository;
import com.api.repository.FoodRepository;
import com.api.service.Imp.FoodServiceImp;
import com.api.utils.FoodKind;
import com.api.utils.FoodStatus;
import com.api.utils.VoucherStatus;
import com.api.utils.VoucherType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodServiceTest {

    @InjectMocks
    private FoodServiceImp foodService;

    @Mock
    private FoodRepository foodRepository;
    @Mock
    private FoodTypeService foodTypeService;
    @Mock
    private RestaurantService restaurantService;
    @Mock
    private FoodMainAndAdditionalRepository foodMainAndAdditionalRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Restaurant mockRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        return restaurant;
    }

    private FoodType mockFoodType(String name) {
        FoodType type = new FoodType();
        type.setName(name);
        type.setFoods(new ArrayList<>());
        return type;
    }

    private Food mockFood(Long id, Restaurant restaurant, FoodKind kind, FoodStatus status) {
        Food food = Food.builder()
                .name("Pizza")
                .kind(kind)
                .status(status)
                .image("image.jpg")
                .restaurant(restaurant)
                .type(mockFoodType("Main"))
                .foodDetails(new ArrayList<>())
                .voucherDetails(new ArrayList<>())
                .mainFoods(new ArrayList<>())
                .additionFoods(new ArrayList<>())
                .build();
        food.setId(1L);
        return food;
    }

    @Test
    void testAddFood_Success() {
        AddFoodRequest request = AddFoodRequest.builder()
                .name("Pizza")
                .description("Yummy")
                .image("image.jpg")
                .kind(FoodKind.MAIN)
                .type("Main")
                .price(BigDecimal.valueOf(10))
                .restaurantId(1L)
                .build();

        Restaurant restaurant = mockRestaurant();
        FoodType foodType = mockFoodType("Main");

        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodTypeService.getFoodTypeByName("Main")).thenReturn(foodType);
        when(foodRepository.existsByRestaurantAndNameAndTypeAndKind(any(), any(), any(), any())).thenReturn(false);

        Food savedFood = mockFood(100L, restaurant, FoodKind.MAIN, FoodStatus.ACTIVE);
        when(foodRepository.save(any())).thenReturn(savedFood);

        long id = foodService.addFood(request);
        assertEquals(1L, id);
    }

    @Test
    void testAdjustFoodPrice_Success() {
        Food food = mockFood(1L, mockRestaurant(), FoodKind.MAIN, FoodStatus.ACTIVE);
        FoodDetail detail = FoodDetail.builder()
                .price(BigDecimal.valueOf(10))
                .startTime(LocalDateTime.now().minusDays(1))
                .build();

        food.getFoodDetails().add(detail);

        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(restaurantService.getRestaurant(1L)).thenReturn(food.getRestaurant());
        when(foodRepository.save(any())).thenReturn(food);

        long result = foodService.adjustFoodPrice(AdjustFoodPriceRequest.builder()
                .foodId(1L)
                .restaurantId(1L)
                .oldPrice(BigDecimal.valueOf(10))
                .newPrice(BigDecimal.valueOf(15))
                .build());

        assertEquals(1L, result);
    }

    @Test
    void testGetCurrentPrice_WithVoucherPercentage() {
        Food food = mockFood(1L, mockRestaurant(), FoodKind.MAIN, FoodStatus.ACTIVE);
        FoodDetail detail = FoodDetail.builder()
                .price(BigDecimal.valueOf(100))
                .startTime(LocalDateTime.now().minusDays(1))
                .build();
        food.getFoodDetails().add(detail);

        Voucher voucher = Voucher.builder()
                .type(VoucherType.PERCENTAGE)
                .value(BigDecimal.valueOf(20))
                .status(VoucherStatus.ACTIVE)
                .restaurant(food.getRestaurant())
                .build();

        VoucherDetail vd = VoucherDetail.builder()
                .voucher(voucher)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        food.getVoucherDetails().add(vd);

        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));

        BigDecimal actualPrice = foodService.getCurrentPrice(1L);
        assertEquals(BigDecimal.valueOf(80), actualPrice);
    }

    @Test
    void testChangeFoodStatus_Success() {
        Food food = mockFood(1L, mockRestaurant(), FoodKind.MAIN, FoodStatus.ACTIVE);

        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(restaurantService.getRestaurant(1L)).thenReturn(food.getRestaurant());

        foodService.changeFoodStatus(1L, 1L, FoodStatus.INACTIVE);
        verify(foodRepository, times(1)).save(food);
        assertEquals(FoodStatus.INACTIVE, food.getStatus());
    }

    @Test
    void testAddAdditionalFoodToFoodOfRestaurant_Valid() {
        Restaurant restaurant = mockRestaurant();
        Food mainFood = mockFood(1L, restaurant, FoodKind.MAIN, FoodStatus.ACTIVE);
        Food add1 = mockFood(2L, restaurant, FoodKind.ADDITIONAL, FoodStatus.ACTIVE);

        when(foodRepository.findById(1L)).thenReturn(Optional.of(mainFood));
        when(foodRepository.findById(2L)).thenReturn(Optional.of(add1));
        when(restaurantService.getRestaurant(1L)).thenReturn(restaurant);
        when(foodMainAndAdditionalRepository.existsByMainFoodAndAdditionFood(mainFood, add1)).thenReturn(false);

        AddAdditionalFoodsRequest request = AddAdditionalFoodsRequest.builder()
                .restaurantId(1L)
                .foodId(1L)
                .additionalFoodIds(Set.of(2))
                .build();

        foodService.addAdditionalFoodToFoodOfRestaurant(request);

        verifyNoInteractions(foodMainAndAdditionalRepository);
    }
}
