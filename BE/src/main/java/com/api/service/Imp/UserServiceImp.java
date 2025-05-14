package com.api.service.Imp;

import com.api.entity.User;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.UserRepository;
import com.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public Long getUserIdByPhone(String phone) {
        return userRepository.findByPhone(phone).get().getId();
    }

    @Override
    public Long getUserIdByPhoneOrEmail(String username) {
        Optional<User> userByPhone = userRepository.findByPhone(username);
        if (userByPhone.isPresent()) {
            return userByPhone.get().getId();
        }

        // If not found by phone, try by email (for Google login)
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,
                        "User not found with phone or email: " + username));
        return user.getId();
    }

    @Override
    public Boolean checkUserExistByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getUserByAccountId(Long accountId) {
        return userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,
                        "User not found with account ID: " + accountId));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,
                        "User not found with ID: " + userId));
    }
}
