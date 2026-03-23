package com.study.loadtest.service.order;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.domain.order.model.Order;
import com.study.loadtest.domain.order.model.OrderStatus;
import com.study.loadtest.repository.event.EventRepository;
import com.study.loadtest.repository.order.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final EventRepository eventRepository;

    @Transactional
    public Order createOrder(Long eventId, Integer quantity) {
        // 남은 티켓 수량 차감
        Event event = eventRepository.findById(eventId);
        event.decreaseQuantity(quantity);

        // 주문 생성
        Order order = Order.builder()
                .event(event)
                .buyerToken(UUID.randomUUID().toString())
                .status(OrderStatus.CREATED)
                .quantity(quantity)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .build();

        return orderRepository.save(order);
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id);
    }
}
