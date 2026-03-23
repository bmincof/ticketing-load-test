package com.study.loadtest.domain.event.model;

import com.study.loadtest.domain.event.exception.SoldOutException;
import com.study.loadtest.domain.order.model.Order;
import com.study.loadtest.repository.BaseJpaEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "event_table")
public class Event extends BaseJpaEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "start_at", nullable = false)
    private OffsetDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private OffsetDateTime endAt;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "remaining_quantity", nullable = false)
    private Integer remainingQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    public void decreaseQuantity(Integer quantity) {
        if (this.remainingQuantity < quantity) {
            throw new SoldOutException(this.getId());
        }
        this.remainingQuantity -= quantity;
    }

    public void increaseQuantity(Integer quantity) {
        this.remainingQuantity += quantity;
    }
}
