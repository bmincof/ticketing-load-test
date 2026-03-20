package com.study.loadtest.interfaces.payment.v1.response;

import com.study.loadtest.domain.payment.model.Payment;
import com.study.loadtest.domain.payment.model.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentResponseV1 {

    private Long id;
    private PaymentStatus status;

    public static PaymentResponseV1 from(Payment payment) {
        return PaymentResponseV1.builder()
                .id(payment.getId())
                .status(payment.getStatus())
                .build();
    }
}
