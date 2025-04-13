package com.api.entity;

import com.api.utils.FoodKind;
import com.api.utils.FoodStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "food")
public class Food extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String image;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodKind kind;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private FoodType type;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Builder.Default
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodDetail> foodDetails = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoucherDetail> voucherDetails = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartDetail> cartDetails = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "mainFood", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMainAndAddition> mainFoods = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "additionFood", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMainAndAddition> additionFoods = new ArrayList<>();
}

