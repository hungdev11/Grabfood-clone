package com.api.jwt;

import com.api.dto.request.AddUserRequest;
import com.api.entity.Account;
import com.api.entity.Role;
import com.api.entity.User;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.AccountRepository;
import com.api.repository.RoleRepository;
import com.api.repository.UserRepository;
import com.api.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private AccountRepository repository;

    private final PasswordEncoder encoder;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Lazy
    private CartService cartService;

    @Autowired
    public UserInfoService(@Lazy PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        GrantedAuthority authority = new SimpleGrantedAuthority(account.getRole().getRoleName());
        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(), // This must be encoded
                List.of(authority)
        );
    }

    public String addAccount(AddUserRequest addUserRequest) {

        // Kiểm tra username (phone) đã tồn tại chưa
        if (this.IsUsernameExisted(addUserRequest.getPhone(), addUserRequest.getEmail())) {
            throw new AppException(ErrorCode.ACCOUNT_USERNAME_DUPLICATED,
                    "Số điện thoại hoặc Email đã được đăng ký");
        }

        // Create and encode the account
        Role role = roleRepository.findByRoleName("ROLE_USER");
        Account account = Account.builder()
                .username(addUserRequest.getPhone())
                .password(encoder.encode(addUserRequest.getPassword()))
                .role(role)
                .build();

        // Create the user and associate it with the account
        User user = User.builder()
                .name(addUserRequest.getName())
                .email(addUserRequest.getEmail())
                .phone(addUserRequest.getPhone())
                .account(account)
                .build();

        userRepository.save(user);
        cartService.createCart(user.getId());
        return "Account Added Successfully";
    }

    public Account registerOAuth2User(String email, String name, String roleName) {

        String randomPhone;
        do {
            // Create 10-digit random phone number
            randomPhone = "0" + String.format("%09d", (int)(Math.random() * 1000000000));
        } while (userRepository.existsByPhone(randomPhone));

        Role role = roleRepository.findByRoleName(roleName);
        Account account = Account.builder()
                .username(email)
                .password(encoder.encode(java.util.UUID.randomUUID().toString())) // Random password
                .role(role)
                .build();

        User user = User.builder()
                .name(name)
                .email(email)
                .phone(randomPhone) // Use the generated unique phone number
                .account(account)
                .build();

        userRepository.save(user);
        cartService.createCart(user.getId());

        return account;
    }

    private boolean IsUsernameExisted(String phone, String email) {
        return repository.existsByUsername(phone) || userRepository.existsByEmail(email);
    }
}
