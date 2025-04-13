package com.api.service.Imp;

import com.api.dto.model.AccountInfoDetails;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private boolean IsUsernameExisted(String phone, String email) {
        return repository.existsByUsername(phone) || userRepository.existsByEmail(email);
    }
}
