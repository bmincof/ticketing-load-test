package com.study.loadtest.infra.jpa.payment;

import com.study.loadtest.domain.payment.model.PaymentStatus;
import com.study.loadtest.infra.jpa.BaseJpaEntity;
import com.study.loadtest.infra.jpa.order.OrderJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "payment_table")
public class PaymentJpaEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;

    @Column(nullable = false)
    private String provider;

    @Column(name = "provider_payment_id", nullable = false)
    private String providerPaymentId;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;
}

