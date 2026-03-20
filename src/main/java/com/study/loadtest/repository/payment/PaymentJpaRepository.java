package com.study.loadtest.repository.payment;

import com.study.loadtest.domain.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
