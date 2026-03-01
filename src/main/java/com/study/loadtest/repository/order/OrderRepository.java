package com.study.loadtest.repository.order;

import com.study.loadtest.domain.order.model.Order;

public interface OrderRepository {

    Order save(Order order);
}
