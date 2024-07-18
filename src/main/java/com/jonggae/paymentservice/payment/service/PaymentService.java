package com.jonggae.paymentservice.payment.service;

import com.jonggae.paymentservice.common.exception.PaymentNotFoundException;
import com.jonggae.paymentservice.payment.dto.PaymentDto;
import com.jonggae.paymentservice.payment.dto.PaymentRequestDto;
import com.jonggae.paymentservice.payment.entity.Payment;
import com.jonggae.paymentservice.payment.entity.PaymentResult;
import com.jonggae.paymentservice.payment.entity.PaymentStatus;
import com.jonggae.paymentservice.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentResult processPayment(PaymentRequestDto paymentRequestDto) {
        validatePaymentRequest(paymentRequestDto);
        boolean paymentSuccess = simulatePaymentProcess();
        Payment payment = createPaymentRecord(paymentRequestDto, paymentSuccess);
        return paymentSuccess ? PaymentResult.SUCCESS : PaymentResult.FAILED;
    }

    private boolean simulatePaymentProcess() {
        return Math.random() < 0.8;
    }

    public PaymentDto getPaymentDetails(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(""));
        return PaymentDto.from(payment);
    }

    private Payment createPaymentRecord(PaymentRequestDto requestDto, boolean success) {
        Payment payment = new Payment();
        payment.setOrderId(requestDto.getOrderId());
        payment.setStatus(success ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        return paymentRepository.save(payment);
    }

    private void validatePaymentRequest(PaymentRequestDto requestDto) {

    }
}
