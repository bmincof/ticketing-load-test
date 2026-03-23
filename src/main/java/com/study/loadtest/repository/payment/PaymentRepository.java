package com.study.loadtest.repository.payment;

import com.study.loadtest.domain.payment.model.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Payment findById(Long id);
}
