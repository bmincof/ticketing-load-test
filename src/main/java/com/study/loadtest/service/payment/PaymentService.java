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

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(Long orderId, Integer amount, String provider) {
        Order order = orderRepository.findById(orderId);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidStateException(Order.class, orderId, order.getStatus().name());
        }

        Payment payment = Payment.builder()
                .order(order)
                .provider(provider)
                .providerPaymentId(UUID.randomUUID().toString())
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment resolve(Long paymentId, PaymentStatus result) {
        Payment payment = paymentRepository.findById(paymentId);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidStateException(Payment.class, paymentId, payment.getStatus().name());
        }

        Order order = payment.getOrder();

        if (result == PaymentStatus.APPROVED) {
            order.setStatus(OrderStatus.PAID);
            payment.setApprovedAt(OffsetDateTime.now());
        } else {
            order.getEvent().increaseQuantity(order.getQuantity());
            order.setStatus(OrderStatus.CANCELED);
        }

        payment.setStatus(result);
        return payment;
    }
}
