package com.study.loadtest.repository.payment;

import com.study.loadtest.domain.payment.model.Payment;
import com.study.loadtest.shared.exception.NoSuchEntityException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PaymentJpaRepositoryWrapper implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(@NonNull Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Payment findById(Long id) {
        return paymentJpaRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException(Payment.class, id));
    }
}
