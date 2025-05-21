package com.api.utils;

import com.api.entity.CartDetail;

import java.math.BigDecimal;
import java.util.List;

public class FeeUtil {
    private static final BigDecimal BASE_FEE = BigDecimal.valueOf(10000); // phí cơ bản
    private static final BigDecimal PER_KM_RATE = BigDecimal.valueOf(3000); // mỗi km thêm bao nhiêu
    private static final BigDecimal LARGE_ORDER_SURCHARGE = BigDecimal.valueOf(15000); // phụ phí đơn lớn

    public static BigDecimal calculateShippingFee(double distanceInKm, List<CartDetail> cartDetails) {
        int totalItems = cartDetails.stream()
                .mapToInt(CartDetail::getQuantity)
                .sum();
        BigDecimal fee = BASE_FEE.add(PER_KM_RATE.multiply(BigDecimal.valueOf(distanceInKm)));

        // Nếu số lượng món > 10, áp dụng phụ phí
        if (totalItems > 10) {
            fee = fee.add(LARGE_ORDER_SURCHARGE);
        }
        return fee;
    }

}
