package com.study.loadtest.service.payment;

import com.study.loadtest.domain.order.model.Order;
import com.study.loadtest.domain.order.model.OrderStatus;
import com.study.loadtest.domain.payment.model.Payment;
import com.study.loadtest.domain.payment.model.PaymentStatus;
import com.study.loadtest.repository.order.OrderRepository;
import com.study.loadtest.repository.payment.PaymentRepository;
import com.study.loadtest.shared.exception.InvalidStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("결제 생성 성공: CREATED 상태 주문에 대해 PENDING 결제가 저장되어야 한다")
    void createPayment_success() {
        // given
        Long orderId = 1L;
        Order order = Order.builder()
                .status(OrderStatus.CREATED)
                .build();
        order.setId(orderId);

        given(orderRepository.findById(orderId)).willReturn(order);
        given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Payment result = paymentService.createPayment(orderId, 10000, "toss");

        // then
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.getAmount()).isEqualTo(10000);
        assertThat(result.getProvider()).isEqualTo("toss");
        assertThat(result.getOrder()).isEqualTo(order);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("결제 생성 실패: 주문 상태가 CREATED가 아니면 InvalidStateException이 발생한다")
    void createPayment_fail_invalidOrderState() {
        // given
        Long orderId = 1L;
        Order order = Order.builder()
                .status(OrderStatus.PAID)
                .build();
        order.setId(orderId);

        given(orderRepository.findById(orderId)).willReturn(order);

        // when & then
        assertThatThrownBy(() -> paymentService.createPayment(orderId, 10000, "toss"))
                .isInstanceOf(InvalidStateException.class);
    }
}
