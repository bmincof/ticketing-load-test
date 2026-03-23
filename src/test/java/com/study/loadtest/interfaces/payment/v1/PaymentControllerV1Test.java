package com.study.loadtest.interfaces.payment.v1;

import com.study.loadtest.domain.payment.model.Payment;
import com.study.loadtest.domain.payment.model.PaymentStatus;
import com.study.loadtest.interfaces.GlobalExceptionHandler;
import com.study.loadtest.interfaces.payment.v1.request.PaymentCreateRequestV1;
import com.study.loadtest.interfaces.payment.v1.request.PaymentResolveRequestV1;
import com.study.loadtest.service.payment.PaymentService;
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
    @DisplayName("결제 승인 성공: APPROVED 결과로 200 OK와 결제 정보를 반환한다")
    void resolvePayment_approved_success() throws Exception {
        // given
        PaymentResolveRequestV1 request = new PaymentResolveRequestV1(PaymentStatus.APPROVED);
        Payment mockPayment = Payment.builder()
                .id(100L)
                .status(PaymentStatus.APPROVED)
                .build();

        given(paymentService.resolve(100L, PaymentStatus.APPROVED)).willReturn(mockPayment);

        // when & then
        mockMvc.perform(post("/api/v1/payments/100/resolve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

}
