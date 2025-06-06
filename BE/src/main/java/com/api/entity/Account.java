package com.api.entity;

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
@Table(name = "account", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class Account extends BaseEntity {

    @Column(columnDefinition = "VARCHAR(30)", nullable = false)
    private String username;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String password;

    @OneToOne(mappedBy = "account")
    private User user;

    @OneToOne(mappedBy = "account")
    private Restaurant restaurant;

    @OneToOne(mappedBy = "account")
    private Shipper shipper;

    @ManyToOne
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;

    @Builder.Default
    @OneToMany(mappedBy = "receivedAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountNotification> notificationDetails = new ArrayList<>();
}
