package com.api.controller;

import com.api.entity.Order;
import com.api.service.Imp.NotificationServiceImp;
import com.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PushNoti {
    private final NotificationServiceImp notificationServiceImp;

    @GetMapping("/push-noti/{resId}")
    public void pushNoti(@PathVariable long resId) {
        notificationServiceImp.sendNewOrderNotification(resId);
    }
}
