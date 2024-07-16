package com.ufm.retailsystems.services;

import com.ufm.retailsystems.configure.payment.VNPayConfig;
import com.ufm.retailsystems.dto.payment.PaymentDTO;
import com.ufm.retailsystems.util.VNPAYUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPayConfig vnPayConfig;
    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request, HttpSession session) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(session);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPAYUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPAYUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPAYUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPAYUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }
}
