package com.api.repository;

import com.api.entity.Account;
import com.api.entity.AccountNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountNotificationRepository extends JpaRepository<AccountNotification, Long> {
    List<AccountNotification> findAllByReceivedAccountOrderByNotification_DateDesc(Account account);

    List<AccountNotification> findTop7ByReceivedAccountAndIsDeletedFalseOrderByNotification_DateDesc(Account account);
}

