package com.study.loadtest.service.payment;

import com.study.loadtest.domain.event.model.Event;
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

    @Test
    @DisplayName("결제 승인 성공: PENDING 결제에 APPROVED 결과를 전달하면 주문이 PAID 상태가 된다")
    void resolve_approved_success() {
        // given
        Long paymentId = 100L;
        Order order = Order.builder()
                .status(OrderStatus.CREATED)
                .quantity(2)
                .build();
        Payment payment = Payment.builder()
                .status(PaymentStatus.PENDING)
                .order(order)
                .build();
        payment.setId(paymentId);

        given(paymentRepository.findById(paymentId)).willReturn(payment);

        // when
        Payment result = paymentService.resolve(paymentId, PaymentStatus.APPROVED);

        // then
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(result.getApprovedAt()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("결제 실패 처리 성공: PENDING 결제에 FAILED 결과를 전달하면 주문이 CANCELED 되고 재고가 복구된다")
    void resolve_failed_success() {
        // given
        Long paymentId = 100L;
        Event event = Event.builder()
                .remainingQuantity(5)
                .totalQuantity(10)
                .build();
        Order order = Order.builder()
                .status(OrderStatus.CREATED)
                .quantity(2)
                .event(event)
                .build();
        Payment payment = Payment.builder()
                .status(PaymentStatus.PENDING)
                .order(order)
                .build();
        payment.setId(paymentId);

        given(paymentRepository.findById(paymentId)).willReturn(payment);

        // when
        Payment result = paymentService.resolve(paymentId, PaymentStatus.FAILED);

        // then
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(event.getRemainingQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("결제 처리 실패: 이미 처리된 결제에 resolve 요청 시 InvalidStateException이 발생한다")
    void resolve_fail_alreadyResolved() {
        // given
        Long paymentId = 100L;
        Payment payment = Payment.builder()
                .status(PaymentStatus.APPROVED)
                .build();
        payment.setId(paymentId);

        given(paymentRepository.findById(paymentId)).willReturn(payment);

        // when & then
        assertThatThrownBy(() -> paymentService.resolve(paymentId, PaymentStatus.APPROVED))
                .isInstanceOf(InvalidStateException.class);
    }
}
