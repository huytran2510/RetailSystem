package com.ufm.retailsystems.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public abstract class PaymentDTO {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor // This will generate a public constructor
    public static class VNPayResponse {
        public String code;
        public String message;
        public String paymentUrl;
    }
}
