package com.study.loadtest.repository.order;

import com.study.loadtest.domain.order.model.Order;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class OrderJpaRepositoryWrapper implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(@NonNull Order order) {
        return orderJpaRepository.save(order);
    }
}
