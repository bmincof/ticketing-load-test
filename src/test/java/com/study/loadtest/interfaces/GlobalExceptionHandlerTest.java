package com.study.loadtest.interfaces;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.shared.exception.NoSuchEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("NoSuchEntityException 발생 시 404 Not Found와 메시지를 반환한다")
    void handleNoSuchEntityException() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("엔티티[Event] 찾을 수 없음 (id=1)"));
    }

    @Test
    @DisplayName("일반 Exception 발생 시 500 Internal Server Error를 반환한다")
    void handleGeneralException() throws Exception {
        mockMvc.perform(get("/test/error"))
                .andExpect(status().isInternalServerError());
    }

    // 테스트용 임시 컨트롤러
    @RestController
    static class TestController {
        @GetMapping("/test/not-found")
        public void throwNotFound() {
            throw new NoSuchEntityException(Event.class, 1L);
        }

        @GetMapping("/test/error")
        public void throwRuntime() {
            throw new RuntimeException("Unexpected error");
        }
    }
}