package com.api.service.Imp;

import com.api.config.MomoConfig;
import com.api.dto.request.MomoRequest;
import com.api.dto.response.MomoResponse;
import com.api.entity.Order;
import com.api.entity.PaymentInfo;
import com.api.repository.OrderRepository;
import com.api.service.MomoPaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class MomoPaymentServiceImp implements MomoPaymentService {

    @Override
    public String createPaymentUrl(Long orderId, BigDecimal amount) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestId = UUID.randomUUID().toString();
            String extraData = "";
            String orderInfo = "Thanh toan don hang " + orderId;

            String rawSignature = String.format(
                    "accessKey=%s&amount=%d&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=captureWallet",
                    MomoConfig.ACCESS_KEY, amount.longValue(), extraData, MomoConfig.IPN_URL, orderId, orderInfo,
                    MomoConfig.PARTNER_CODE, MomoConfig.REDIRECT_URL, requestId);

            String signature = new HmacUtils("HmacSHA256", MomoConfig.SECRET_KEY)
                    .hmacHex(rawSignature);

            MomoRequest request = MomoRequest.builder()
                    .amount(amount.longValue())
                    .redirectUrl(MomoConfig.REDIRECT_URL)
                    .requestType("captureWallet")
                    .requestId(requestId)
                    .ipnUrl(MomoConfig.IPN_URL)
                    .partnerCode(MomoConfig.PARTNER_CODE)
                    .extraData(extraData)
                    .orderId(orderId.toString())
                    .orderInfo(orderInfo)
                    .signature(signature)
                    .build();

            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(request);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.postForObject(MomoConfig.ENDPOINT, entity, String.class );

            MomoResponse momoResponse = mapper.readValue(response, MomoResponse.class);

            System.out.println("Response: " + response);
            log.info("MoMo API Response: " + response);
            if (momoResponse != null && momoResponse.getPayUrl() != null) {
                return momoResponse.getPayUrl();
            }
            throw new RuntimeException("Failed to create MOMO payment URL");
        } catch (Exception e) {
            throw new RuntimeException("Error creating MOMO payment: " + e.getMessage());
        }
    }


}
