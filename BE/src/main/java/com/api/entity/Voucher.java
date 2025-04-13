package com.api.entity;

import com.api.entity.BaseEntity;
import com.api.utils.VoucherStatus;
import com.api.utils.VoucherType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String code;

    private String description;

    @Column(nullable = false)
    private int quantity;

    private BigDecimal minRequire;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VoucherType type;

    private BigDecimal value;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VoucherStatus status;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = true)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoucherDetail> voucherDetails = new ArrayList<>();
}
