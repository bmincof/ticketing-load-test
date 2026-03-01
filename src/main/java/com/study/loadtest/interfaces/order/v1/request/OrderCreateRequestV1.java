package com.study.loadtest.interfaces.order.v1.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateRequestV1 {

    private Long eventId;
    private Integer quantity;
}
