package com.api.controller;

import com.api.dto.request.AddAdditionalFoodsRequest;
import com.api.dto.request.AddFoodRequest;
import com.api.dto.request.AdjustFoodPriceRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.GetFoodResponse;
import com.api.dto.response.PageResponse;
import com.api.service.FoodService;
import com.api.utils.FoodStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodControllerTest {

    private FoodController foodController;
    private FoodService foodService;

    private GetFoodResponse food1;
    private GetFoodResponse food2;
    private List<GetFoodResponse> items;
    private PageResponse<List<GetFoodResponse>> pageResponse;

    @BeforeEach
    void setUp() {
        foodService = mock(FoodService.class);
        foodController = new FoodController(foodService);

        food1 = new GetFoodResponse();
        food1.setId(1L);
        food1.setName("Pizza");
        food1.setPrice(BigDecimal.valueOf(10));

        food2 = new GetFoodResponse();
        food2.setId(2L);
        food2.setName("Burger");
        food2.setPrice(BigDecimal.valueOf(8));

        items = List.of(food1, food2);

        pageResponse = PageResponse.<List<GetFoodResponse>>builder()
                .page(0)
                .size(20)
                .total(2)
                .items(items)
                .build();
    }

    @Test
    void testAddNewFood() {
        AddFoodRequest request = new AddFoodRequest();
        request.setName("Pizza");
        request.setPrice(BigDecimal.valueOf(9.99));
        request.setRestaurantId(1L);

        when(foodService.addFood(request)).thenReturn(100L);

        ApiResponse<Long> response = foodController.addNewFood(request);

        assertEquals(202, response.getCode());
        assertEquals(100L, response.getData());
    }

    @Test
    void testAdjustFoodPrice() {
        AdjustFoodPriceRequest request = new AdjustFoodPriceRequest();
        request.setFoodId(1L);
        request.setNewPrice(BigDecimal.valueOf(12.99));

        when(foodService.adjustFoodPrice(request)).thenReturn(1L);

        ApiResponse<Long> response = foodController.adjustFoodPrice(request);

        assertEquals(202, response.getCode());
        assertEquals(1L, response.getData());
    }

    @Test
    void testGetFood() {
        when(foodService.getFood(1L, false)).thenReturn(food1);

        ApiResponse<GetFoodResponse> response = foodController.getFood(1L, false);

        assertEquals(200, response.getCode());
        assertEquals("Pizza", response.getData().getName());
    }

    @Test
    void testUpdateFoodStatus() {
        doNothing().when(foodService).changeFoodStatus(1L, 1L, FoodStatus.ACTIVE);

        ApiResponse<?> response = foodController.updateFoodStatus(1L, 1L, FoodStatus.ACTIVE);

        assertEquals(200, response.getCode());
    }

    @Test
    void testGetAdditionalFoodsOfRestaurant() {
        when(foodService.getAdditionalFoodsOfRestaurant(1L, false, 0, 5)).thenReturn(pageResponse);

        ApiResponse<?> response = foodController.getAdditionalFoodsOfRestaurant(1L, 0, 5, false);

        assertEquals(200, response.getCode());
        assertEquals(2, ((PageResponse<?>) response.getData()).getTotal());
    }

    @Test
    void testGetAdditionalFoodsOfFood() {
        when(foodService.getAdditionalFoodsOfFood(1L, 2L, false, 0, 5)).thenReturn(pageResponse);

        ApiResponse<?> response = foodController.getAdditionalFoodsOfFood(1L, 2L, 0, 5, false);

        assertEquals(200, response.getCode());
        assertEquals(2, ((PageResponse<?>) response.getData()).getTotal());
    }

    @Test
    void testAddAdditionalFoodsToFood() {
        AddAdditionalFoodsRequest request = new AddAdditionalFoodsRequest();
        request.setFoodId(1L);
        request.setRestaurantId(1L);
        request.setAdditionalFoodIds(Set.of(2));

        doNothing().when(foodService).addAdditionalFoodToFoodOfRestaurant(request);

        ApiResponse<?> response = foodController.addAdditionalFoodsToFood(request);

        assertEquals(200, response.getCode());
    }
}
