package com.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "reviews")
@Entity
public class Review extends BaseEntity {
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
    private Order order;

    @Column(nullable = false, name = "order_string")
    private String orderString;

    @Column(name = "review_time", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private BigDecimal rating;

    @Column(name = "review_message")
    private String reviewMessage;

    @Column(name = "reply_time")
    private LocalDateTime replyTime;

    @Column(name = "reply_message")
    private String replyMessage;
}
