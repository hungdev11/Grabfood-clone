package com.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "food_main_addtion_detail", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"mainFood", "additionFood"})
})
public class FoodMainAndAddition extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "main_food_id", nullable = false)
    private Food mainFood;

    @ManyToOne
    @JoinColumn(name = "addition_food_id", nullable = false)
    private Food additionFood;
}
