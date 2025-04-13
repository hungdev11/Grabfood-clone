package com.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "food_detail")
public class FoodDetail extends BaseEntity{

    @Column(nullable = false, precision = 9, scale = 2) //example: 9.999.999,99
    private BigDecimal price;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;
}
