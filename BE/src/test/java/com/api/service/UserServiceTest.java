package com.api.service;

import com.api.entity.Account;
import com.api.entity.Role;
import com.api.entity.User;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.UserRepository;
import com.api.service.Imp.UserServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImp userService;

    private User testUser;
    private Account testAccount;
    private Role testRole;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        testRole = new Role();
        testRole.setId(1L);
        testRole.setRoleName("ROLE_USER");

        // Initialize test account
        testAccount = Account.builder()
                .username("testAccount")
                .password("encodedPassword")
                .role(testRole)
                .build();
        testAccount.setId(1L); // Set ID explicitly
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setPhone("0123456789");
        testUser.setEmail("test@example.com");
        testUser.setAccount(testAccount);
    }

    @Test
    @DisplayName("Should get user ID by phone number")
    void getUserIdByPhone_Success() {
        // Arrange
        String phone = "0123456789";
        when(userRepository.findByPhone(phone)).thenReturn(Optional.of(testUser));

        // Act
        Long userId = userService.getUserIdByPhone(phone);

        // Assert
        assertEquals(1L, userId);
        verify(userRepository).findByPhone(phone);
    }

    @Test
    @DisplayName("Should get user ID by phone when username is phone number")
    void getUserIdByPhoneOrEmail_WithPhone_Success() {
        // Arrange
        String username = "0123456789";
        when(userRepository.findByPhone(username)).thenReturn(Optional.of(testUser));

        // Act
        Long userId = userService.getUserIdByPhoneOrEmail(username);

        // Assert
        assertEquals(1L, userId);
        verify(userRepository).findByPhone(username);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should get user ID by email when username is email")
    void getUserIdByPhoneOrEmail_WithEmail_Success() {
        // Arrange
        String username = "test@example.com";
        when(userRepository.findByPhone(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(testUser));

        // Act
        Long userId = userService.getUserIdByPhoneOrEmail(username);

        // Assert
        assertEquals(1L, userId);
        verify(userRepository).findByPhone(username);
        verify(userRepository).findByEmail(username);
    }

    @Test
    @DisplayName("Should throw exception when user not found by phone or email")
    void getUserIdByPhoneOrEmail_UserNotFound() {
        // Arrange
        String username = "nonexistent";
        when(userRepository.findByPhone(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            userService.getUserIdByPhoneOrEmail(username);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(username));
        verify(userRepository).findByPhone(username);
        verify(userRepository).findByEmail(username);
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void checkUserExistByEmail_Success() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act
        Boolean exists = userService.checkUserExistByEmail(email);

        // Assert
        assertTrue(exists);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("Should check if user does not exist by email")
    void checkUserExistByEmail_NotExists() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // Act
        Boolean exists = userService.checkUserExistByEmail(email);

        // Assert
        assertFalse(exists);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("Should get user by account ID")
    void getUserByAccountId_Success() {
        // Arrange
        Long accountId = 100L;
        when(userRepository.findByAccountId(accountId)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserByAccountId(accountId);

        // Assert
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getPhone(), result.getPhone());
        verify(userRepository).findByAccountId(accountId);
    }

    @Test
    @DisplayName("Should throw exception when user not found by account ID")
    void getUserByAccountId_UserNotFound() {
        // Arrange
        Long accountId = 999L;
        when(userRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            userService.getUserByAccountId(accountId);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(accountId.toString()));
        verify(userRepository).findByAccountId(accountId);
    }

    @Test
    @DisplayName("Should get user by ID")
    void getUserById_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getPhone(), result.getPhone());
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void getUserById_UserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains(userId.toString()));
        verify(userRepository).findById(userId);
    }
}