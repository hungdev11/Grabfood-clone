package com.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PushNoti {
    private final NotificationController notificationController;

    @GetMapping("/push-noti")
    public void pushNoti() {
        notificationController.sendNewOrderNotification(1, null);
    }
}
