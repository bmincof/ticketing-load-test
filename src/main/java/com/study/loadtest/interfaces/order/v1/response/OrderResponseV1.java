package com.study.loadtest.interfaces.order.v1.response;

import com.study.loadtest.domain.order.model.Order;
import com.study.loadtest.domain.order.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Builder
@Getter
public class OrderResponseV1 {

    private Long id;
    private String buyerToken;
    private OffsetDateTime expiresAt;
    private OrderStatus status;

    public static OrderResponseV1 from(Order order) {
        return OrderResponseV1.builder()
                .id(order.getId())
                .buyerToken(order.getBuyerToken())
                .expiresAt(order.getExpiresAt())
                .status(order.getStatus())
                .build();
    }
}
