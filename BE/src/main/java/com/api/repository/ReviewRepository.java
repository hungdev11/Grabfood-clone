package com.api.repository;

import com.api.entity.Order;
import com.api.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByOrder (Order order);
    List<Review> findAllByOrderIn(List<Order> orders);
}
