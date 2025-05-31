package com.api.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MomoConfig {
    public static final String PARTNER_CODE = "MOMOLRJZ20181206"; // Thay bằng Partner Code của bạn
    public static final String ACCESS_KEY = "mTCKt9W3eU1m39TW";     // Thay bằng Access Key
    public static final String SECRET_KEY = "SetA5RDnLHvt51AULf51DyauxUo3kDU6";     // Thay bằng Secret Key
    public static final String ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create"; // Test endpoint
    public static final String REDIRECT_URL = "http://localhost:6969/grab/payments/momo/callback"; // URL trả về
    public static final String IPN_URL = "https://36f3-14-169-66-187.ngrok-free.app/grab/payments/momo/notify";

}
