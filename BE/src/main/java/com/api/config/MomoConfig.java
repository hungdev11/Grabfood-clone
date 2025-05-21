package com.api.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MomoConfig {
    public static final String PARTNER_CODE = "MOMO"; // Thay bằng Partner Code của bạn
    public static final String ACCESS_KEY = "F8BBA842ECF85";     // Thay bằng Access Key
    public static final String SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";     // Thay bằng Secret Key
    public static final String ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create"; // Test endpoint
    public static final String REDIRECT_URL = "http://localhost:6969/grab/payments/momo/callback"; // URL trả về
    public static final String IPN_URL = "https://http://192.168.1.7:6969/grab/payments/momo/notify";

}
