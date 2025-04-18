package com.api.entity;

import com.api.utils.ListLongToStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(
        name = "cart_detail",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"cart_id", "food_id", "order_id", "ids"})
        }
)
//@Check(constraints = "quantity > 0")
public class CartDetail extends BaseEntity{

    @Column(nullable = false)
    private int quantity;

    private String note;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "ids", columnDefinition = "VARCHAR(255)") // Hoặc độ dài phù hợp với nhu cầu của bạn
    @Convert(converter = ListLongToStringConverter.class)
    private List<Long> ids;
}
