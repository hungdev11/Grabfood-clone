package com.api.service;

import com.api.entity.Account;
import com.api.entity.Role;
import com.api.entity.User;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.AccountRepository;
import com.api.repository.PasswordResetTokenRepository;
import com.api.repository.RoleRepository;
import com.api.repository.UserRepository;
import com.api.service.Imp.AccountServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AccountServiceImp accountService;

    private Account testAccount;
    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        // Setup test data
        testRole = new Role();
        testRole.setId(1L);
        testRole.setRoleName("ROLE_USER");

        testAccount = Account.builder()
                .username("0123456789")
                .password("encodedPassword")
                .role(testRole)
                .build();
        testAccount.setId(1L); // Set ID explicitly

        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .phone("0123456789")
                .account(testAccount)
                .build();
        testUser.setId(1L); // Set ID explicitly

        testAccount.setUser(testUser);
    }

    @Test
    void addNewAccount_Success() {
        // Arrange
        when(accountRepository.existsByUsername("newuser")).thenReturn(false);
        when(roleRepository.findByRoleName("ROLE_RES")).thenReturn(testRole);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Important: Capture the saved account and set its ID
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account savedAccount = invocation.getArgument(0);
            savedAccount.setId(1L); // Manually set ID as it would happen in the database
            return savedAccount;
        });

        // Act
        long accountId = accountService.addNewAccount("newuser", "password");

        // Assert
        assertEquals(1L, accountId);
        verify(accountRepository).existsByUsername("newuser");
        verify(roleRepository).findByRoleName("ROLE_RES");
        verify(passwordEncoder).encode("password");
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void addNewAccount_DuplicateUsername_ThrowsException() {
        // Arrange
        when(accountRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            accountService.addNewAccount("existinguser", "password");
        });

        assertEquals(ErrorCode.ACCOUNT_USERNAME_DUPLICATED, exception.getErrorCode());
        verify(accountRepository).existsByUsername("existinguser");
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void checkAccount_Success() {
        // Arrange
        when(accountRepository.findByUsername("0123456789")).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);

        // Act
        accountService.checkAccount("0123456789", "correctPassword");

        // Assert
        verify(accountRepository).findByUsername("0123456789");
        verify(passwordEncoder).matches("correctPassword", "encodedPassword");
    }

    @Test
    void checkAccount_UsernameNotExist_ThrowsException() {
        // Arrange
        when(accountRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            accountService.checkAccount("nonexistent", "anyPassword");
        });

        assertEquals(ErrorCode.ACCOUNT_USERNAME_NOT_EXISTED, exception.getErrorCode());
        verify(accountRepository).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void checkAccount_IncorrectPassword_ThrowsException() {
        // Arrange
        when(accountRepository.findByUsername("0123456789")).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            accountService.checkAccount("0123456789", "wrongPassword");
        });

        assertEquals(ErrorCode.ACCOUNT_PASSWORD_NOT_MATCH, exception.getErrorCode());
        verify(accountRepository).findByUsername("0123456789");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    }

    @Test
    void getAccountById_ExistingId_ReturnsAccount() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        Account result = accountService.getAccountById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("0123456789", result.getUsername());
        verify(accountRepository).findById(1L);
    }

    @Test
    void getAccountById_NonExistingId_ThrowsException() {
        // Arrange
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            accountService.getAccountById(999L);
        });

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
        verify(accountRepository).findById(999L);
    }

    @Test
    void getAccountByUsername_ExistingUsername_ReturnsAccount() {
        // Arrange
        when(accountRepository.findByUsername("0123456789")).thenReturn(Optional.of(testAccount));

        // Act
        Account result = accountService.getAccountByUsername("0123456789");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("0123456789", result.getUsername());
        verify(accountRepository).findByUsername("0123456789");
    }

    @Test
    void getAccountByUsername_NonExistingUsername_ThrowsException() {
        // Arrange
        when(accountRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            accountService.getAccountByUsername("nonexistent");
        });

        assertEquals(ErrorCode.ACCOUNT_USERNAME_NOT_EXISTED, exception.getErrorCode());
        verify(accountRepository).findByUsername("nonexistent");
    }

    @Test
    void changePassword_ValidCurrentPassword_ChangesPassword() {
        // Arrange
        String currentPassword = "oldPassword";
        String newPassword = "newPassword";

        when(accountRepository.findByUsername("0123456789")).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches(currentPassword, testAccount.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        boolean result = accountService.changePassword("0123456789", currentPassword, newPassword);

        // Assert
        assertTrue(result);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        assertEquals("newEncodedPassword", accountCaptor.getValue().getPassword());
    }

    @Test
    void changePassword_InvalidCurrentPassword_ThrowsException() {
        // Arrange
        String currentPassword = "wrongPassword";
        String newPassword = "newPassword";

        when(accountRepository.findByUsername("0123456789")).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.matches(currentPassword, testAccount.getPassword())).thenReturn(false);

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            accountService.changePassword("0123456789", currentPassword, newPassword);
        });

        assertEquals(ErrorCode.INVALID_PASSWORD, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Current password is incorrect"));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void changePassword_AccountNotFound_ThrowsException() {
        // Arrange
        String currentPassword = "oldPassword";
        String newPassword = "newPassword";

        when(accountRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            accountService.changePassword("nonexistent", currentPassword, newPassword);
        });

        assertEquals(ErrorCode.ACCOUNT_USERNAME_NOT_EXISTED, exception.getErrorCode());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(accountRepository, never()).save(any(Account.class));
    }
}