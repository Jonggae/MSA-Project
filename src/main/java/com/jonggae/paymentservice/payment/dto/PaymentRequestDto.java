package com.jonggae.paymentservice.payment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class PaymentRequestDto {
    private Long orderId;
    private Long amount;
    private String paymentMethod;
}
