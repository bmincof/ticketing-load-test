package com.study.loadtest.interfaces.payment.v1;

import com.study.loadtest.domain.payment.model.Payment;
import com.study.loadtest.interfaces.payment.v1.request.PaymentCreateRequestV1;
import com.study.loadtest.interfaces.payment.v1.response.PaymentResponseV1;
import com.study.loadtest.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@RestController
public class PaymentControllerV1 {

    private final PaymentService paymentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PaymentResponseV1 createPayment(@RequestBody PaymentCreateRequestV1 request) {
        Payment payment = paymentService.createPayment(request.getOrderId(), request.getAmount());
        return PaymentResponseV1.from(payment);
    }
}
