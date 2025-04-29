package com.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PushNoti {
    private final NotificationController notificationController;

    @GetMapping("/push-noti/{id}")
    public void pushNoti(@PathVariable long id) {
        notificationController.sendNewOrderNotification(id, null);
    }
}
