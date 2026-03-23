package com.study.loadtest.integration;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.domain.event.model.EventStatus;
import com.study.loadtest.domain.order.model.Order;
import com.study.loadtest.domain.order.model.OrderStatus;
import com.study.loadtest.domain.payment.model.Payment;
import com.study.loadtest.domain.payment.model.PaymentStatus;
import com.study.loadtest.interfaces.order.v1.request.OrderCreateRequestV1;
import com.study.loadtest.interfaces.payment.v1.request.PaymentCreateRequestV1;
import com.study.loadtest.interfaces.payment.v1.request.PaymentResolveRequestV1;
import com.study.loadtest.repository.event.EventJpaRepository;
import com.study.loadtest.repository.order.OrderJpaRepository;
import com.study.loadtest.repository.payment.PaymentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TicketingIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EventJpaRepository eventJpaRepository;
    @Autowired private OrderJpaRepository orderJpaRepository;
    @Autowired private PaymentJpaRepository paymentJpaRepository;

    @BeforeEach
    void setUp() {
        paymentJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
        eventJpaRepository.deleteAll();
    }

    // --- helpers ---

    private Event saveEvent(int totalQuantity, int remainingQuantity) {
        return eventJpaRepository.save(Event.builder()
                .name("통합테스트 이벤트")
                .startAt(OffsetDateTime.now())
                .endAt(OffsetDateTime.now().plusDays(1))
                .totalQuantity(totalQuantity)
                .remainingQuantity(remainingQuantity)
                .status(EventStatus.ACTIVE)
                .build());
    }

    private Order saveOrder(Event event, int quantity, OrderStatus status) {
        return orderJpaRepository.save(Order.builder()
                .event(event)
                .buyerToken(UUID.randomUUID().toString())
                .quantity(quantity)
                .status(status)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .build());
    }

    private Payment savePayment(Order order, PaymentStatus status) {
        return paymentJpaRepository.save(Payment.builder()
                .order(order)
                .provider("toss")
                .providerPaymentId(UUID.randomUUID().toString())
                .amount(10000)
                .status(status)
                .build());
    }

    // --- GET /events/{id} ---

    @Test
    @DisplayName("GET /events/{id}: 이벤트 조회 성공")
    void getEvent_success() throws Exception {
        Event event = saveEvent(100, 100);

        mockMvc.perform(get("/api/v1/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(event.getId()))
                .andExpect(jsonPath("$.name").value("통합테스트 이벤트"))
                .andExpect(jsonPath("$.totalQuantity").value(100))
                .andExpect(jsonPath("$.remainingQuantity").value(100))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /events/{id}: 존재하지 않는 이벤트 조회 시 404 반환")
    void getEvent_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/events/9999"))
                .andExpect(status().isNotFound());
    }

    // --- POST /orders ---

    @Test
    @DisplayName("POST /orders: 주문 생성 성공 - 재고 차감 확인")
    void createOrder_success() throws Exception {
        Event event = saveEvent(10, 10);
        OrderCreateRequestV1 request = new OrderCreateRequestV1(event.getId(), 3);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"));

        Event updated = eventJpaRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getRemainingQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("POST /orders: 존재하지 않는 이벤트 주문 시 404 반환")
    void createOrder_eventNotFound() throws Exception {
        OrderCreateRequestV1 request = new OrderCreateRequestV1(9999L, 1);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /orders: 재고 부족 시 409 반환")
    void createOrder_soldOut() throws Exception {
        Event event = saveEvent(2, 1);
        OrderCreateRequestV1 request = new OrderCreateRequestV1(event.getId(), 2);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // --- GET /orders/{id} ---

    @Test
    @DisplayName("GET /orders/{id}: 주문 조회 성공")
    void getOrder_success() throws Exception {
        Event event = saveEvent(10, 10);
        Order order = saveOrder(event, 1, OrderStatus.CREATED);

        mockMvc.perform(get("/api/v1/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("GET /orders/{id}: 존재하지 않는 주문 조회 시 404 반환")
    void getOrder_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/orders/9999"))
                .andExpect(status().isNotFound());
    }

    // --- POST /payments ---

    @Test
    @DisplayName("POST /payments: 결제 생성 성공")
    void createPayment_success() throws Exception {
        Event event = saveEvent(10, 10);
        Order order = saveOrder(event, 1, OrderStatus.CREATED);
        PaymentCreateRequestV1 request = new PaymentCreateRequestV1(order.getId(), 10000, "toss");

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /payments: 존재하지 않는 주문으로 결제 시 404 반환")
    void createPayment_orderNotFound() throws Exception {
        PaymentCreateRequestV1 request = new PaymentCreateRequestV1(9999L, 10000, "toss");

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /payments: CREATED 상태가 아닌 주문으로 결제 시 409 반환")
    void createPayment_invalidOrderState() throws Exception {
        Event event = saveEvent(10, 9);
        Order order = saveOrder(event, 1, OrderStatus.PAID);
        PaymentCreateRequestV1 request = new PaymentCreateRequestV1(order.getId(), 10000, "toss");

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // --- POST /payments/{id}/resolve ---

    @Test
    @DisplayName("POST /payments/{id}/resolve APPROVED: 결제 승인 - 주문 상태 PAID 변경")
    void resolvePayment_approved() throws Exception {
        Event event = saveEvent(10, 9);
        Order order = saveOrder(event, 1, OrderStatus.CREATED);
        Payment payment = savePayment(order, PaymentStatus.PENDING);

        mockMvc.perform(post("/api/v1/payments/{id}/resolve", payment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentResolveRequestV1(PaymentStatus.APPROVED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        Order updatedOrder = orderJpaRepository.findById(order.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("POST /payments/{id}/resolve FAILED: 결제 실패 - 주문 CANCELED, 재고 복구")
    void resolvePayment_failed() throws Exception {
        Event event = saveEvent(10, 9);  // 잔여 재고 9 (이미 1 차감된 상태)
        Order order = saveOrder(event, 1, OrderStatus.CREATED);
        Payment payment = savePayment(order, PaymentStatus.PENDING);

        mockMvc.perform(post("/api/v1/payments/{id}/resolve", payment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentResolveRequestV1(PaymentStatus.FAILED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"));

        Order updatedOrder = orderJpaRepository.findById(order.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);

        Event updatedEvent = eventJpaRepository.findById(event.getId()).orElseThrow();
        assertThat(updatedEvent.getRemainingQuantity()).isEqualTo(10);  // 9 + 1 복구
    }

    @Test
    @DisplayName("POST /payments/{id}/resolve: 존재하지 않는 결제 요청 시 404 반환")
    void resolvePayment_notFound() throws Exception {
        mockMvc.perform(post("/api/v1/payments/9999/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentResolveRequestV1(PaymentStatus.APPROVED))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /payments/{id}/resolve: 이미 처리된 결제 요청 시 409 반환")
    void resolvePayment_alreadyResolved() throws Exception {
        Event event = saveEvent(10, 9);
        Order order = saveOrder(event, 1, OrderStatus.PAID);
        Payment payment = savePayment(order, PaymentStatus.APPROVED);

        mockMvc.perform(post("/api/v1/payments/{id}/resolve", payment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PaymentResolveRequestV1(PaymentStatus.APPROVED))))
                .andExpect(status().isConflict());
    }
}
