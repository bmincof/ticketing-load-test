package com.study.loadtest.interfaces.payment.v1.request;

import com.study.loadtest.domain.payment.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResolveRequestV1 {

    private PaymentStatus result;
}
