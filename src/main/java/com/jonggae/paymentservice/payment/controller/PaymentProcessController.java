package com.jonggae.paymentservice.payment.controller;

import com.jonggae.paymentservice.payment.dto.PaymentDto;
import com.jonggae.paymentservice.payment.dto.PaymentRequestDto;
import com.jonggae.paymentservice.payment.entity.PaymentResult;
import com.jonggae.paymentservice.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentProcessController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResult> processPayment(@RequestBody PaymentRequestDto paymentRequestDto){
        PaymentResult result = paymentService.processPayment(paymentRequestDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPaymentDetails(@PathVariable Long paymentId) {
        PaymentDto paymentDto = paymentService.getPaymentDetails(paymentId);
        return ResponseEntity.ok(paymentDto);
    }
}
