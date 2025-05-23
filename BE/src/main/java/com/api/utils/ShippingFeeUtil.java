package com.api.utils;

import java.math.BigDecimal;

public class ShippingFeeUtil {
    private static final double BASE_DISTANCE = 3000;
    private static final double BASE_FEE = 13000;
    private static final double PER_KM_RATE = 4000;

    public static BigDecimal calculateShippingFee(double distance)
    {
        if(distance< 0) return BigDecimal.ZERO;
        if(distance <= BASE_DISTANCE)
        {
            return BigDecimal.valueOf(BASE_FEE);
        } else {
            int fee = (int) (BASE_FEE + (distance - BASE_DISTANCE) / 1000 * PER_KM_RATE);
            return BigDecimal.valueOf((fee /1000) * 1000);
        }
    }
}
