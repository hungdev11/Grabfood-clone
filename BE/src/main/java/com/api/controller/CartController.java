package com.api.controller;

import com.api.dto.request.AddToCartRequest;
import com.api.dto.request.CartUpdateRequest;
import com.api.dto.request.DeleteCartItemRequest;
import com.api.dto.response.ApiResponse;
import com.api.dto.response.CartResponse;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.service.CartService;
import com.api.jwt.JwtService;
import com.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(@RequestParam Long userId, @RequestBody AddToCartRequest request) {
        cartService.addToCart(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cartId}/remove/{foodId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartId, @PathVariable Long foodId, @RequestParam List<Long> additionalItems) {
        cartService.removeFromCart(cartId, foodId, additionalItems);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ApiResponse<CartResponse> getAllCartDetailUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Authorization header is missing or invalid");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        Long userId = userService.getUserIdByPhoneOrEmail(username);
        return ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Success")
                .data(cartService.getAllCartDetailUser(userId))
                .build();
    }

    @GetMapping("/test")
    public ApiResponse<CartResponse> getCartTest() {
        return ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Success")
                .data(cartService.getAllCartDetailUser(2L))
                .build();
    }

    @PutMapping("/update-quantity")
    public void updateQuantity(@RequestBody CartUpdateRequest request) {
        cartService.updateCartDetailQuantity(request);
    }

    @PutMapping("/update")
    public void update(@RequestBody CartUpdateRequest request) {
        cartService.updateCart(request);
    }

    @DeleteMapping("/delete")
    public void deleteCartItem(@RequestBody DeleteCartItemRequest request) {
        cartService.removeFromCart(request.getUserId(), request.getFoodId(), request.getAdditionalFoodIds());
    }

    @DeleteMapping
    public void deleteCartDetail(@RequestParam long cartDetailId) {
        cartService.deleteCartDetail(cartDetailId);
    }

    @GetMapping("/checkOpen")
    public ResponseEntity<Boolean> checkRestaurantOpen(@RequestParam long cartId)
    {
        boolean check = cartService.checkRestaurantOpen(cartId);
        return ResponseEntity.ok(check);
    }
}
