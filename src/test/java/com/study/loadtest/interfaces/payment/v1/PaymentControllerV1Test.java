package com.study.loadtest.interfaces.payment.v1;

import com.study.loadtest.domain.payment.model.Payment;
import com.study.loadtest.domain.payment.model.PaymentStatus;
import com.study.loadtest.interfaces.GlobalExceptionHandler;
import com.study.loadtest.interfaces.payment.v1.request.PaymentCreateRequestV1;
import com.study.loadtest.service.payment.PaymentService;
import com.study.loadtest.shared.exception.InvalidStateException;
import com.study.loadtest.shared.exception.NoSuchEntityException;
import com.study.loadtest.domain.order.model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentControllerV1.class)
@Import(GlobalExceptionHandler.class)
class PaymentControllerV1Test {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @DisplayName("성공: 올바른 요청 시 201 Created와 결제 정보를 반환한다")
    void createPayment_success() throws Exception {
        // given
        PaymentCreateRequestV1 request = new PaymentCreateRequestV1(1L, 10000, "toss");

        Payment mockPayment = Payment.builder()
                .id(100L)
                .status(PaymentStatus.PENDING)
                .build();

        given(paymentService.createPayment(1L, 10000, "toss")).willReturn(mockPayment);

        // when & then
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("404: 존재하지 않는 주문 id 요청 시 Not Found를 반환한다")
    void createPayment_notFound() throws Exception {
        // given
        PaymentCreateRequestV1 request = new PaymentCreateRequestV1(999L, 10000, "toss");
        given(paymentService.createPayment(999L, 10000, "toss"))
                .willThrow(new NoSuchEntityException(Order.class, 999L));

        // when & then
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("409: 유효하지 않은 주문 상태 요청 시 Conflict를 반환한다")
    void createPayment_invalidState() throws Exception {
        // given
        PaymentCreateRequestV1 request = new PaymentCreateRequestV1(1L, 10000, "toss");
        given(paymentService.createPayment(1L, 10000, "toss"))
                .willThrow(new InvalidStateException(Order.class, 1L, "PAID"));

        // when & then
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
