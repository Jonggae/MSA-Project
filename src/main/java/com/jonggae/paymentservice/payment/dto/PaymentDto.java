package com.jonggae.paymentservice.payment.dto;

import com.jonggae.paymentservice.payment.entity.Payment;
import com.jonggae.paymentservice.payment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentDto {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus status;
    private LocalDateTime createdAt;

    public static PaymentDto from(Payment payment) {
       return PaymentDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt()).build();

    }
}
