package com.adrian.paymentservice.service.interf;

import com.adrian.paymentservice.dto.request.PaymentRequest;

public interface IPaymentService {

    void processPayment(PaymentRequest request);
}
