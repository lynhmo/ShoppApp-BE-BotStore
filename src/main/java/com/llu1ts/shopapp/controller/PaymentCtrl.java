package com.llu1ts.shopapp.controller;

import com.google.gson.Gson;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentCtrl {

    @Data
    static class PaymentRequest {
        private int paymentType;
        private long amount;
        private long orderId;
    }

    enum PaymentType {
        NASPAS(1,"payWithATM"),
        CREDIT_CARD(2,"payWithCC"),;

        PaymentType(int id, String type) {
            this.id = id;
            this.type = type;
        }

        private final String type;
        @Getter
        private final int id;

        // Method to get PaymentType by ID
        public static String getTypeById(int id) {
            for (PaymentType paymentType : PaymentType.values()) {
                if (paymentType.getId() == id) {
                    return paymentType.type;
                }
            }
            throw new IllegalArgumentException("No PaymentType with id: " + id);
        }
    }

    @Value("${momo.url}")
    private String returnUrl;

    @Autowired
    private Gson gson;


    @PostMapping("/momo")
    public ResponseEntity<?> createMomoPayment(@RequestBody PaymentRequest paymentRequest) {

        String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
        String partnerCode = "MOMO";
        String accessKey = "F8BBA842ECF85";
        String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
        String requestId = partnerCode + "_" + UUID.randomUUID();
        String orderId = String.valueOf(paymentRequest.getOrderId()) + UUID.randomUUID();
        String orderInfo = "pay-with-momo";
        String redirectUrl = returnUrl;
        String ipnUrl = "https://callback.url/notify";
        String requestType = PaymentType.getTypeById(paymentRequest.paymentType);
        long amount = paymentRequest.getAmount();
        String extraData = String.valueOf(paymentRequest.getOrderId());
//        String extraData = Collections.singletonMap("orderId",paymentRequest.getOrderId()); // Optional

        // Tạo chữ ký (signature)
        // Tạo path param
        String rawData = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;
        String signature = HmacSHA256(secretKey, rawData);

        // Tạo đối tượng chứa thông tin yêu cầu
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", partnerCode);
        requestBody.put("accessKey", accessKey);
        requestBody.put("requestId", requestId);
        requestBody.put("amount", amount);
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", redirectUrl);
        requestBody.put("ipnUrl", ipnUrl);
        requestBody.put("extraData", extraData);
        requestBody.put("requestType", requestType);
        requestBody.put("signature", signature);
        requestBody.put("lang", "vi");

        // Gửi yêu cầu đến MoMo
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);


        // Gọi bằng POST
        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);

        // Xử lý phản hồi từ MoMo
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if ("0".equals(responseBody.get("resultCode").toString())) {
                String payUrl = responseBody.get("payUrl").toString();
                return ResponseEntity.ok(Collections.singletonMap("payUrl", payUrl));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment");
        }
    }

    private String HmacSHA256(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC SHA256", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
