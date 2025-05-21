package com.api.service;

import com.api.dto.request.AddToCartRequest;
import com.api.dto.request.CartUpdateRequest;

import com.api.dto.response.CartDetailResponse;
import com.api.dto.response.CartResponse;
import com.api.entity.Cart;

import java.util.List;

public interface CartService {
    void createCart(Long userId);
    void addToCart(Long userId, AddToCartRequest addToCartRequest);
    void removeFromCart(Long cartId, Long foodId, List<Long> additionalItems);
    void updateCart(CartUpdateRequest request);
//    BigDecimal calculateTotalPrice(Long cartId);
    void updateCartDetailQuantity(CartUpdateRequest request);
    CartResponse getAllCartDetailUser(Long userId);
    void clearCart(Cart cart);
    void deleteCartDetail(Long cartDetailId);
    boolean checkRestaurantOpen(long cartId);
}
