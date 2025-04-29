package com.api.service.Imp;

import com.api.dto.request.AddUserRequest;
import com.api.entity.PasswordResetToken;
import com.api.entity.Role;
import com.api.entity.User;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.entity.Account;
import com.api.repository.AccountRepository;
import com.api.repository.PasswordResetTokenRepository;
import com.api.repository.RoleRepository;
import com.api.repository.UserRepository;
import com.api.service.AccountService;
import com.api.service.EmailService;
import com.api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImp implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private static final long PASSWORD_RESET_EXPIRATION = 1; // hours

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

    @Override
    public Account getAccountByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElse(null);
    }

    @Override
    public boolean changePassword(String username, String currentPassword, String newPassword) {
        Account account = getAccountByUsername(username);

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "Current password is incorrect");
        }

        // Update with new encoded password
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        return true;
    }

    @Override
    public String generatePasswordResetToken(String email) {
//        Account account = getAccountByUsername(username);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found"));
        Account account = user.getAccount();
        if(account.getUsername().equalsIgnoreCase(email)){
            throw new AppException(
                    ErrorCode.GOOGLE_ACCOUNT_NO_PASSWORD,
                    "Tài khoản này đăng nhập bằng Google. Không thể đặt lại mật khẩu."
            );
        }

        // Generate random token
        String token = UUID.randomUUID().toString();

        // Delete any existing tokens for this account
        passwordResetTokenRepository.deleteByAccountId(account.getId());

        // Create and save new token
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .account(account)
                .expiryDate(LocalDateTime.now().plusHours(PASSWORD_RESET_EXPIRATION))
                .build();
        passwordResetTokenRepository.save(resetToken);

        // Send email
        emailService.sendPasswordResetEmail(email, token);

        return token;
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN, "Invalid password reset token"));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new AppException(ErrorCode.EXPIRED_TOKEN, "Password reset token has expired");
        }

        return true;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN, "Invalid password reset token"));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new AppException(ErrorCode.EXPIRED_TOKEN, "Password reset token has expired");
        }

        Account account = resetToken.getAccount();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        // Delete used token
        passwordResetTokenRepository.delete(resetToken);
    }

    private boolean IsUsernameExisted(String username) {
        log.info("Check if username {} exists", username);
        return accountRepository.existsByUsername(username);
    }
}
