package com.study.loadtest.interfaces.payment.v1.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequestV1 {

    private Long orderId;
    private Integer amount;
    private String provider;
}
