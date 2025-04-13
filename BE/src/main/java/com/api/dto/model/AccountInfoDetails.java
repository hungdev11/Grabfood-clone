package com.api.dto.model;

import com.api.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AccountInfoDetails implements UserDetails {
    private String username; // Changed from 'name' to 'email' for clarity
    private String password;
    private List<GrantedAuthority> authorities;

    public AccountInfoDetails(Account userInfo) {
        this.username = userInfo.getUsername(); // Use email as username
        this.password = userInfo.getPassword();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(userInfo.getRole().getRoleName()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
