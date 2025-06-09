package com.api.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MomoConfig {
    public static final String PARTNER_CODE = "MOMOLRJZ20181206";
    public static final String ACCESS_KEY = "mTCKt9W3eU1m39TW";
    public static final String SECRET_KEY = "SetA5RDnLHvt51AULf51DyauxUo3kDU6";
    public static final String ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";
    public static final String REDIRECT_URL = "grabfoodapp://momo-return";
    public static final String IPN_URL =  "https://5ce1-2001-ee0-1b38-d48d-3066-759d-3a71-f14.ngrok-free.app/grab/payments/momo/notify";

}
