package com.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address")
public class Address extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String province;
    private String district;
    private String ward;
    private String detail;
    private boolean isDefault;
}
