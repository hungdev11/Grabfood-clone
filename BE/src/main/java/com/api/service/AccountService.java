package com.api.service;

import com.api.dto.request.AddUserRequest;
import com.api.entity.Account;

public interface AccountService {
    long addNewAccount(String username, String password);
    void checkAccount(String username, String password);
    public Account getAccountById(long id);
    Account getAccountByUsername(String username);
    boolean changePassword(String username, String currentPassword, String newPassword);
    String generatePasswordResetToken(String email);
    boolean validatePasswordResetToken(String token);
    void resetPassword(String token, String newPassword);
}
