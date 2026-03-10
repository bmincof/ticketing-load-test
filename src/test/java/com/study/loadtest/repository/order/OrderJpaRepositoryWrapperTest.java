package com.study.loadtest.repository.order;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.domain.event.model.EventStatus;
import com.study.loadtest.domain.order.model.Order;
import com.study.loadtest.domain.order.model.OrderStatus;
import com.study.loadtest.repository.event.EventJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(OrderJpaRepositoryWrapper.class)
class OrderJpaRepositoryWrapperTest {

    @Autowired
    private OrderJpaRepositoryWrapper orderRepositoryWrapper;

    @Autowired
    private EventJpaRepository eventJpaRepository;

    @Test
    @DisplayName("Order 객체를 저장하면 DB에 정상적으로 영속화되어야 한다")
    void save_success() {
        // given: 1. 외래키 제약조건을 위해 Event 먼저 생성 및 저장
        Event event = Event.builder()
                .name("테스트 이벤트")
                .startAt(OffsetDateTime.now())
                .endAt(OffsetDateTime.now().plusDays(1))
                .totalQuantity(100)
                .remainingQuantity(100)
                .status(EventStatus.ACTIVE)
                .build();
        Event savedEvent = eventJpaRepository.save(event);

        // given: 2. Order 객체 생성 (Builder 사용)
        Order order = Order.builder()
                .event(savedEvent)
                .buyerToken(UUID.randomUUID().toString())
                .status(OrderStatus.CREATED)
                .quantity(2)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .build();

        // when
        Order savedOrder = orderRepositoryWrapper.save(order);

        // then
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getEvent().getId()).isEqualTo(savedEvent.getId());
        assertThat(savedOrder.getQuantity()).isEqualTo(2);
        assertThat(savedOrder.getCreatedAt()).isNotNull();
    }
}