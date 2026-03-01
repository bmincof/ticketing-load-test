package com.study.loadtest.interfaces.order.v1;

import com.study.loadtest.domain.order.model.Order;
import com.study.loadtest.interfaces.order.v1.request.OrderCreateRequestV1;
import com.study.loadtest.interfaces.order.v1.response.OrderResponseV1;
import com.study.loadtest.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/api/v1/orders")
public class OrderControllerV1 {

    private final OrderService orderService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderResponseV1 createOrder(@RequestBody OrderCreateRequestV1 request) {
        Order order = orderService.createOrder(request.getEventId(), request.getQuantity());
        return OrderResponseV1.from(order);
    }
}
