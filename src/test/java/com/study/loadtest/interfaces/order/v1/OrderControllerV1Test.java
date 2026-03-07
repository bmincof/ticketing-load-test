package com.study.loadtest.interfaces.order.v1;

import com.study.loadtest.domain.order.model.Order;
import com.study.loadtest.interfaces.GlobalExceptionHandler;
import com.study.loadtest.interfaces.order.v1.request.OrderCreateRequestV1;
import com.study.loadtest.service.order.OrderService;
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

@WebMvcTest(OrderControllerV1.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerV1Test {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private OrderService orderService;

    @Test
    @DisplayName("성공: 올바른 요청 시 201 Created를 반환한다")
    void createOrder_success() throws Exception {
        // given
        OrderCreateRequestV1 request = new OrderCreateRequestV1(1L, 5);
        Order mockOrder = Order.builder().build();
        mockOrder.setId(100L);
        given(orderService.createOrder(1L, 5)).willReturn(mockOrder);

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L));
    }
}