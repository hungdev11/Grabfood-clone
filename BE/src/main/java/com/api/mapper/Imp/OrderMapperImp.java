package com.api.mapper.Imp;

import com.api.dto.response.CartDetailResponse;
import com.api.dto.response.OrderResponse;
import com.api.entity.CartDetail;
import com.api.entity.Food;
import com.api.entity.FoodDetail;
import com.api.entity.Order;
import com.api.mapper.OrderMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapperImp implements OrderMapper {

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public OrderResponse toOrderResponse(Order order) {
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        List<CartDetailResponse> cartDetailResponses = order.getCartDetails().stream()
                .map(this::convertToCartDetailResponse)
                .collect(Collectors.toList());
        orderResponse.setCartDetails(cartDetailResponses);
        return  orderResponse;
    }
    private CartDetailResponse convertToCartDetailResponse(CartDetail cartDetail) {
        CartDetailResponse cartDetailResponse = modelMapper.map(cartDetail, CartDetailResponse.class);
        Food food = cartDetail.getFood();
        cartDetailResponse.setId(food.getId());
        FoodDetail currentFoodDetail = cartDetail.getFood().getFoodDetails().stream()
                .filter(foodDetail -> foodDetail.getEndTime()==null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Khong tim thay food detail gia tri hien tai"));
        cartDetailResponse.setPrice(currentFoodDetail.getPrice());
        return cartDetailResponse;
    }
}
