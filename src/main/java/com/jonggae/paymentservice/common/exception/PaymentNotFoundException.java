package com.jonggae.paymentservice.common.exception;

public class PaymentNotFoundException extends RuntimeException{
    public PaymentNotFoundException(String message){
        super(message);
    }
}
