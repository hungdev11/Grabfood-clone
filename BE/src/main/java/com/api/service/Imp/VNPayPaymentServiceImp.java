package com.api.service.Imp;

import com.api.config.VNPayConfig;
import com.api.service.VNPayPaymentService;
import com.api.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VNPayPaymentServiceImp implements VNPayPaymentService {
    private final VNPayConfig vnPayConfig;

    @Override
    public String createPaymentUrl(HttpServletRequest request, BigDecimal amount, Long orderId) {
        long amountInVND = amount.multiply(BigDecimal.valueOf(100)).longValue(); // convert to VND * 100
        String bankCode = request.getParameter("bankCode");

        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_TxnRef",  String.valueOf(orderId));
        vnpParamsMap.put("vnp_Amount", String.valueOf(amountInVND));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        return vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
    }
}
