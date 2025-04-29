package com.api.repository;

import com.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    Optional<User> findByPhone(String phone);

    @Query(value = "SELECT * FROM `grab-food`.user WHERE id = (SELECT user_id FROM `grab-food`.cart WHERE id = :cartId)", nativeQuery = true)
    User findUserByCartId(@Param("cartId") Long cartId);
    Optional<User> findByAccountId(Long accountId);
}
