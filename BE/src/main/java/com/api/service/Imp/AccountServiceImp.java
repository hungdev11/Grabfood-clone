package com.api.service.Imp;

import com.api.dto.request.AddUserRequest;
import com.api.entity.Role;
import com.api.entity.User;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.entity.Account;
import com.api.repository.AccountRepository;
import com.api.repository.RoleRepository;
import com.api.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImp implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public long addNewAccount(String username, String password) {
        log.info("Add new account");

        if (this.IsUsernameExisted(username)) {
            log.error("Account already exists with username {} ", username);
            throw new AppException(ErrorCode.ACCOUNT_USERNAME_DUPLICATED);
        }

        Role role = roleRepository.findByRoleName("ROLE_RES");

        Account createdAccount = Account.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        accountRepository.save(createdAccount);

        return createdAccount.getId();
    }

    @Override
    public void checkAccount(String username, String rawPassword) {
        log.info("Check account");
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Username {} doesn't exist", username);
                    return new AppException(ErrorCode.ACCOUNT_USERNAME_NOT_EXISTED);
                });

        // So sánh mật khẩu raw với mật khẩu đã encode trong DB
        if (!passwordEncoder.matches(rawPassword, account.getPassword())) {
            log.error("Password is incorrect");
            throw new AppException(ErrorCode.ACCOUNT_PASSWORD_NOT_MATCH);
        }
    }

    @Override
    public Account getAccountById(long id) {
        log.info("Get account");
        return accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Account id {} doesn't existed: ", id);
                    return new AppException(ErrorCode.RESOURCE_NOT_FOUND);
                });
    }

    private boolean IsUsernameExisted(String username) {
        log.info("Check if username {} exists", username);
        return accountRepository.existsByUsername(username);
    }
}
