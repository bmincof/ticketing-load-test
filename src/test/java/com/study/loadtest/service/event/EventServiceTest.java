package com.study.loadtest.service.event;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.repository.event.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Test
    @DisplayName("이벤트 ID로 조회 시, 레포지토리가 반환한 이벤트를 그대로 반환해야 한다")
    void readEvent_success() {
        // given
        Long eventId = 1L;
        Event mockEvent = new Event();
        mockEvent.setName("테스트 이벤트");

        // Mock 객체의 동작 정의 (Stubbing)
        given(eventRepository.findById(eventId)).willReturn(mockEvent);

        // when
        Event result = eventService.readEvent(eventId);

        // then
        assertThat(result.getName()).isEqualTo("테스트 이벤트");
        verify(eventRepository).findById(eventId);
    }
}