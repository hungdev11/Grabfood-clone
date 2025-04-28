package com.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    NOT_FOUND(404, "Resource not found", HttpStatus.NOT_FOUND),
    //RESET PASSWORD
    INVALID_TOKEN(1000, "Invalid token", HttpStatus.BAD_REQUEST),
    EXPIRED_TOKEN(1001, "Token has expired", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1002, "Invalid password", HttpStatus.BAD_REQUEST),
    // JWT
    FORBIDDEN(403, "Access forbidden", HttpStatus.FORBIDDEN),
    UNAUTHORIZED(401, "Unauthorized access", HttpStatus.UNAUTHORIZED),
    SOME_THING_WENT_WRONG(9999, "Not OK", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND(404, "Not Found", HttpStatus.NOT_FOUND),
    //ACCOUNT
    ACCOUNT_USERNAME_DUPLICATED(0000, "Username of an account is duplicated", HttpStatus.BAD_REQUEST),
    ACCOUNT_USERNAME_NOT_EXISTED(0001, "Username of an account not found", HttpStatus.BAD_REQUEST),
    ACCOUNT_PASSWORD_NOT_MATCH(0002, "Wrong password", HttpStatus.BAD_REQUEST),
    //FOOD - FOOD TYPE
    FOODTYPE_NAME_EXISTED(0050, "Type of food is already existed", HttpStatus.BAD_REQUEST),
    FOODTYPE_NAME_NOT_EXISTED(0051, "Type of food not found", HttpStatus.BAD_REQUEST),
    FOOD_OF_RETAURANT_EXISTED(0052, "Food type and kind of retaurant already existed", HttpStatus.BAD_REQUEST),
    FOOD_PRICE_REDUNDANT(0053, "Food price redundant", HttpStatus.BAD_REQUEST),
    FOOD_RESTAURANT_NOT_FOUND(0054, "Food not belong to restaurant", HttpStatus.BAD_REQUEST),
    FOOD_NOT_FOUND(0055, "Food not found", HttpStatus.BAD_REQUEST),
    FOOD_DETAIL_CONFLICT_PRICE(0056, "Food price conflict", HttpStatus.BAD_REQUEST),
    FOOD_NOT_PUBLIC_FOR_CUSTOMER(0055, "Food not found", HttpStatus.BAD_REQUEST),
    FOOD_ADDITIONAL(0056, "Food is additional", HttpStatus.BAD_REQUEST),
    ADDITIONAL_FOOD_NOT_FOUND(0057, "Additional food not found or belong to food", HttpStatus.BAD_REQUEST),
    //RESTAURANT
    RESTAURANT_NOT_FOUND(0100, "Restaurant not found", HttpStatus.BAD_REQUEST),
    //VOUCHER
    VOUCHER_NOT_FOUND(0100, "Voucher not found", HttpStatus.BAD_REQUEST),
    VOUCHER_VALUE_CONFLICT(0200, "Voucher value is conflict", HttpStatus.BAD_REQUEST),
    VOUCHER_ID_EXISTED(0201, "The voucher ID has already been used and cannot be processed", HttpStatus.BAD_REQUEST),
    VOUCHER_EXPIRED(0202, "Voucher expired", HttpStatus.BAD_REQUEST),
    VOUCHER_MIN_REQUIRE(0203, "Order price is less than min require", HttpStatus.BAD_REQUEST),

    USER_NOT_FOUND(0300, "User not found", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(0301, "Cart not found", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND(0302, "Cart item not found", HttpStatus.BAD_REQUEST),
    CART_EMPTY(0301, "Cart is empty", HttpStatus.BAD_REQUEST),
    INVALID_TIME(0700, "Invalid time", HttpStatus.BAD_REQUEST),

    ORDER_NOT_FOUND(0400, "Order not found", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND(0450, "Review not found", HttpStatus.BAD_REQUEST),
    CANNOT_WRITE_REVIEW_DUE_DATE(0451, "Can't write review due date", HttpStatus.BAD_REQUEST),
    ;
    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
