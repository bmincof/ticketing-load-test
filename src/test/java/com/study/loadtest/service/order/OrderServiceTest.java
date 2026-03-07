package com.study.loadtest.service.order;

import com.study.loadtest.domain.event.exception.SoldOutException;
import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.domain.order.model.Order;
import com.study.loadtest.domain.order.model.OrderStatus;
import com.study.loadtest.repository.event.EventRepository;
import com.study.loadtest.repository.order.OrderRepository;
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
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock private EventRepository eventRepository;

    @Test
    @DisplayName("주문 생성 성공: 재고가 차감되고 주문이 저장되어야 한다")
    void createOrder_success() {
        // given
        Long eventId = 1L;
        int requestQuantity = 2;

        Event event = new Event();
        event.setRemainingQuantity(10); // 초기 재고 10개

        given(eventRepository.findById(eventId)).willReturn(event);
        // save 호출 시 전달받은 객체를 그대로 반환하도록 설정
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Order result = orderService.createOrder(eventId, requestQuantity);

        // then
        assertThat(event.getRemainingQuantity()).isEqualTo(8); // 재고 차감 확인
        assertThat(result.getQuantity()).isEqualTo(requestQuantity);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 생성 실패: 재고가 부족하면 SoldOutException이 발생한다")
    void createOrder_fail_soldOut() {
        // given
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        event.setRemainingQuantity(1); // 재고 1개

        given(eventRepository.findById(eventId)).willReturn(event);

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(eventId, 5))
                .isInstanceOf(SoldOutException.class);
    }
}