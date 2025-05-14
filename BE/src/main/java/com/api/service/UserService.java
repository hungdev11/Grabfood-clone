package com.api.service;

import com.api.entity.User;

public interface UserService {
    Long getUserIdByPhone(String phone);
    Long getUserIdByPhoneOrEmail(String username);
    Boolean checkUserExistByEmail(String email);
    User getUserByAccountId(Long accountId);
    User getUserById(Long userId);
}
