package com.study.loadtest.interfaces.event.v1;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.interfaces.GlobalExceptionHandler;
import com.study.loadtest.service.event.EventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventControllerV1.class)
@Import(GlobalExceptionHandler.class)
class EventControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    @DisplayName("존재하는 이벤트 조회 시 200 OK와 데이터를 반환한다")
    void getEvent_success() throws Exception {
        // given
        Long eventId = 1L;
        Event event = new Event();
        event.setName("테스트 이벤트");
        given(eventService.readEvent(eventId)).willReturn(event);

        // when & then
        mockMvc.perform(get("/api/v1/events/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("테스트 이벤트"));
    }
}