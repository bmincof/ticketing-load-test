package com.study.loadtest.service.payment;

import com.study.loadtest.domain.order.model.Order;
import com.study.loadtest.domain.order.model.OrderStatus;
import com.study.loadtest.domain.payment.model.Payment;
import com.study.loadtest.domain.payment.model.PaymentStatus;
import com.study.loadtest.repository.order.OrderRepository;
import com.study.loadtest.repository.payment.PaymentRepository;
import com.study.loadtest.shared.exception.InvalidStateException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(Long orderId, Integer amount) {
        Order order = orderRepository.findById(orderId);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidStateException(Order.class, orderId, order.getStatus().name());
        }

        Payment payment = Payment.builder()
                .order(order)
                .provider("mock")
                .providerPaymentId(UUID.randomUUID().toString())
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }
}
