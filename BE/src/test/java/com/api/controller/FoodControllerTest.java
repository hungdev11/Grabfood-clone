package com.api.controller;

import com.api.dto.request.AddAdditionalFoodsRequest;
import com.api.dto.request.AddFoodRequest;
import com.api.dto.request.AdjustFoodPriceRequest;
import com.api.dto.request.UpdateFoodInfoRequest;
import com.api.dto.response.GetFoodGroupResponse;
import com.api.dto.response.GetFoodResponse;
import com.api.dto.response.PageResponse;
import com.api.dto.response.SearchResultResponse;
import com.api.entity.Food;
import com.api.service.FoodService;
import com.api.service.Imp.FoodServiceImp;
import com.api.utils.FoodStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.naming.directory.SearchResult;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FoodControllerTest {

    @Mock
    private FoodServiceImp foodService;

    @InjectMocks
    private FoodController foodController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(foodController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void addNewFood_ShouldReturnSuccessResponse() throws Exception {
        // Given
        AddFoodRequest request = new AddFoodRequest();
        Long expectedFoodId = 1L;
        when(foodService.addFood(any(AddFoodRequest.class))).thenReturn(expectedFoodId);

        // When & Then
        mockMvc.perform(post("/foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(202))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").value(expectedFoodId));

        verify(foodService).addFood(any(AddFoodRequest.class));
    }

    @Test
    void adjustFoodPrice_ShouldReturnSuccessResponse() throws Exception {
        // Given
        AdjustFoodPriceRequest request = new AdjustFoodPriceRequest();
        Long expectedResult = 1L;
        when(foodService.adjustFoodPrice(any(AdjustFoodPriceRequest.class))).thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(post("/foods/adjust-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(202))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").value(expectedResult));

        verify(foodService).adjustFoodPrice(any(AdjustFoodPriceRequest.class));
    }

    @Test
    void getFood_WithDefaultIsForCustomer_ShouldReturnFood() throws Exception {
        // Given
        long foodId = 1L;
        GetFoodResponse expectedResponse = new GetFoodResponse();
        when(foodService.getFood(foodId, false)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/foods/{foodId}", foodId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).getFood(foodId, false);
    }

    @Test
    void getFood_WithIsForCustomerTrue_ShouldReturnFood() throws Exception {
        // Given
        long foodId = 1L;
        GetFoodResponse expectedResponse = new GetFoodResponse();
        when(foodService.getFood(foodId, true)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/foods/{foodId}", foodId)
                        .param("isForCustomer", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).getFood(foodId, true);
    }

    @Test
    void getFoodsOfRestaurant_WithDefaultParams_ShouldReturnFoods() throws Exception {
        // Given
        long restaurantId = 1L;
        GetFoodGroupResponse expectedResponse = new GetFoodGroupResponse();
        when(foodService.getFoodGroupOfRestaurant(restaurantId, false)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/foods/restaurant/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).getFoodGroupOfRestaurant(restaurantId, false);
    }

    @Test
    void getFoodsOfRestaurant_WithIsForCustomerTrue_ShouldReturnFoods() throws Exception {
        // Given
        long restaurantId = 1L;
        GetFoodGroupResponse expectedResponse = new GetFoodGroupResponse();
        when(foodService.getFoodGroupOfRestaurant(restaurantId, true)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/foods/restaurant/{restaurantId}", restaurantId)
                        .param("isForCustomer", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).getFoodGroupOfRestaurant(restaurantId, true);
    }

    @Test
    void getAllFoodsOfRestaurant_ShouldReturnAllFoods() throws Exception {
        // Given
        long restaurantId = 1L;
        List<GetFoodResponse> expectedResponse = List.of(new GetFoodResponse());
        when(foodService.getFoodsOfRestaurant(restaurantId)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/foods/all/restaurant/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).getFoodsOfRestaurant(restaurantId);
    }

    @Test
    void updateFoodStatus_ShouldReturnSuccessResponse() throws Exception {
        // Given
        long foodId = 1L;
        long restaurantId = 1L;
        FoodStatus foodStatus = FoodStatus.ACTIVE;

        // When & Then
        mockMvc.perform(put("/foods/{foodId}", foodId)
                        .param("restaurantId", String.valueOf(restaurantId))
                        .param("foodStatus", foodStatus.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).changeFoodStatus(restaurantId, foodId, foodStatus);
    }

    @Test
    void deleteFood_ShouldReturnSuccessResponse() throws Exception {
        // Given
        long foodId = 1L;

        // When & Then
        mockMvc.perform(delete("/foods/{foodId}", foodId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).deleteFood(foodId);
    }

    @Test
    void updateFoodInfo_ShouldReturnSuccessResponse() throws Exception {
        // Given
        long foodId = 1L;
        long restaurantId = 1L;
        UpdateFoodInfoRequest request = new UpdateFoodInfoRequest();
        request.setNewPrice(Optional.of(BigDecimal.valueOf(50000)));
        when(foodService.getFoodByIdAndRestaurantId(foodId, restaurantId)).thenReturn(new Food());
        // When & Then
        System.out.println(objectMapper.writeValueAsString(request));
        mockMvc.perform(put("/foods/info/{foodId}", foodId)
                        .param("restaurantId", String.valueOf(restaurantId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).updateFoodInfo(restaurantId, foodId, any(UpdateFoodInfoRequest.class));
    }

    @Test
    void getAdditionalFoodsOfRestaurant_WithDefaultParams_ShouldReturnFoods() throws Exception {
        // Given
        long restaurantId = 1L;
        PageResponse<List<GetFoodResponse>> expectedResponse = PageResponse.<List<GetFoodResponse>>builder()
                .items(List.of(new GetFoodResponse()))
                .build();
        when(foodService.getAdditionalFoodsOfRestaurant(restaurantId, false, 0, 100)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/foods/additional")
                        .param("restaurantId", String.valueOf(restaurantId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).getAdditionalFoodsOfRestaurant(restaurantId, false, 0, 100);
    }

    @Test
    void getAdditionalFoodsOfRestaurant_WithCustomParams_ShouldReturnFoods() throws Exception {
        // Given
        long restaurantId = 1L;
        int page = 1;
        int pageSize = 50;
        boolean isForCustomer = true;
        PageResponse<List<GetFoodResponse>> expectedResponse = PageResponse.<List<GetFoodResponse>>builder()
                .items(List.of(new GetFoodResponse()))
                .build();
        when(foodService.getAdditionalFoodsOfRestaurant(restaurantId, isForCustomer, page, pageSize)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/foods/additional")
                        .param("restaurantId", String.valueOf(restaurantId))
                        .param("page", String.valueOf(page))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("isForCustomer", String.valueOf(isForCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).getAdditionalFoodsOfRestaurant(restaurantId, isForCustomer, page, pageSize);
    }

    @Test
    void getAdditionalFoodsOfFood_WithDefaultParams_ShouldReturnFoods() throws Exception {
        // Given
        long foodId = 1L;
        long restaurantId = 1L;
        PageResponse<List<GetFoodResponse>> expectedResponse = PageResponse.<List<GetFoodResponse>>builder()
                .items(List.of(new GetFoodResponse()))
                .build();
        when(foodService.getAdditionalFoodsOfFood(restaurantId, foodId, false, 0, 5)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/foods/additional/{foodId}", foodId)
                        .param("restaurantId", String.valueOf(restaurantId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).getAdditionalFoodsOfFood(restaurantId, foodId, false, 0, 5);
    }

    @Test
    void addAdditionalFoodsToFood_ShouldReturnSuccessResponse() throws Exception {
        // Given
        AddAdditionalFoodsRequest request = new AddAdditionalFoodsRequest();
        request.setFoodId(1); // hoặc set các field khác nếu cần
        request.setRestaurantId(2);
        request.setAdditionalFoodIds(Set.of(3, 4));

        // When & Then
        mockMvc.perform(post("/foods/additional")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).addAdditionalFoodToFoodOfRestaurant(
                argThat(req ->
                        req.getFoodId() == request.getFoodId() &&
                                req.getRestaurantId() == request.getRestaurantId() &&
                                Objects.equals(req.getAdditionalFoodIds(), request.getAdditionalFoodIds())
                )
        );
    }

    @Test
    void getFoodPriceIn_ShouldReturnPrice() throws Exception {
        // Given
        long foodId = 1L;
        LocalDateTime time = LocalDateTime.now();
        BigDecimal expectedPrice = BigDecimal.valueOf(100);
        when(foodService.getFoodPriceIn(foodId, time)).thenReturn(expectedPrice);

        // When & Then
        mockMvc.perform(get("/foods/price")
                        .param("foodId", String.valueOf(foodId))
                        .param("time", time.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).getFoodPriceIn(foodId, time);
    }

    @Test
    void searchFoods_WithRestaurantId_ShouldReturnSearchResults() throws Exception {
        // Given
        String query = "pizza";
        Long restaurantId = 1L;
        boolean isForCustomer = true;
        List<GetFoodResponse> expectedResponse = List.of(new GetFoodResponse());
        when(foodService.searchFoods(query, restaurantId, isForCustomer)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/foods/search")
                        .param("query", query)
                        .param("restaurantId", String.valueOf(restaurantId))
                        .param("isForCustomer", String.valueOf(isForCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).searchFoods(query, restaurantId, isForCustomer);
    }

    @Test
    void searchFoods_WithoutRestaurantId_ShouldReturnSearchResults() throws Exception {
        // Given
        String query = "pizza";
        boolean isForCustomer = false;
        SearchResultResponse expectedResult = new SearchResultResponse();
        when(foodService.searchFoodsAndRestaurants(query, isForCustomer)).thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(get("/foods/search")
                        .param("query", query)
                        .param("isForCustomer", String.valueOf(isForCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(foodService).searchFoodsAndRestaurants(query, isForCustomer);
    }
}