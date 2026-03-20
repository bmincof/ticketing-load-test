package com.study.loadtest.repository.order;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.domain.event.model.EventStatus;
import com.study.loadtest.domain.order.model.Order;
import com.study.loadtest.domain.order.model.OrderStatus;
import com.study.loadtest.repository.event.EventJpaRepository;
import com.study.loadtest.shared.exception.NoSuchEntityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    @DisplayName("findById: 존재하는 Order를 정상적으로 조회한다")
    void findById_success() {
        // given
        Event event = eventJpaRepository.save(Event.builder()
                .name("테스트 이벤트")
                .startAt(OffsetDateTime.now())
                .endAt(OffsetDateTime.now().plusDays(1))
                .totalQuantity(100)
                .remainingQuantity(100)
                .status(EventStatus.ACTIVE)
                .build());

        Order saved = orderRepositoryWrapper.save(Order.builder()
                .event(event)
                .buyerToken(UUID.randomUUID().toString())
                .status(OrderStatus.CREATED)
                .quantity(1)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .build());

        // when
        Order found = orderRepositoryWrapper.findById(saved.getId());

        // then
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    @DisplayName("findById: 존재하지 않는 id 조회 시 NoSuchEntityException이 발생한다")
    void findById_notFound() {
        assertThatThrownBy(() -> orderRepositoryWrapper.findById(999L))
                .isInstanceOf(NoSuchEntityException.class);
    }
}