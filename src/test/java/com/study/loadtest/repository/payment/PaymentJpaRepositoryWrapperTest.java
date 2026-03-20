package com.study.loadtest.repository.payment;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.domain.event.model.EventStatus;
import com.study.loadtest.domain.order.model.Order;
import com.study.loadtest.domain.order.model.OrderStatus;
import com.study.loadtest.domain.payment.model.Payment;
import com.study.loadtest.domain.payment.model.PaymentStatus;
import com.study.loadtest.repository.event.EventJpaRepository;
import com.study.loadtest.repository.order.OrderJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PaymentJpaRepositoryWrapper.class)
class PaymentJpaRepositoryWrapperTest {

    @Autowired
    private PaymentJpaRepositoryWrapper paymentRepositoryWrapper;

    @Autowired
    private EventJpaRepository eventJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Test
    @DisplayName("Payment 객체를 저장하면 DB에 정상적으로 영속화되어야 한다")
    void save_success() {
        // given: Event → Order 순으로 선행 데이터 생성
        Event event = eventJpaRepository.save(Event.builder()
                .name("테스트 이벤트")
                .startAt(OffsetDateTime.now())
                .endAt(OffsetDateTime.now().plusDays(1))
                .totalQuantity(100)
                .remainingQuantity(100)
                .status(EventStatus.ACTIVE)
                .build());

        Order order = orderJpaRepository.save(Order.builder()
                .event(event)
                .buyerToken(UUID.randomUUID().toString())
                .status(OrderStatus.CREATED)
                .quantity(1)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .build());

        Payment payment = Payment.builder()
                .order(order)
                .provider("toss")
                .providerPaymentId(UUID.randomUUID().toString())
                .amount(10000)
                .status(PaymentStatus.PENDING)
                .build();

        // when
        Payment saved = paymentRepositoryWrapper.save(payment);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getOrder().getId()).isEqualTo(order.getId());
        assertThat(saved.getProvider()).isEqualTo("toss");
        assertThat(saved.getAmount()).isEqualTo(10000);
        assertThat(saved.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
