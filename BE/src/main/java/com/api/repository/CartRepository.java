package com.api.repository;

import com.api.entity.Cart;
import com.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    Optional<Cart> findByUser (User user);
}
