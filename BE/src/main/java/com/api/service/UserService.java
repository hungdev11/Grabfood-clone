package com.api.service;

public interface UserService {
    Long getUserIdByPhone(String phone);
    Long getUserIdByPhoneOrEmail(String username);
}
