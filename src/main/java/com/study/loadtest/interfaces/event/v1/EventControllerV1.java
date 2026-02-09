package com.study.loadtest.interfaces.event.v1;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.interfaces.event.v1.response.EventResponseV1;
import com.study.loadtest.service.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/api/v1/events")
public class EventControllerV1 {

    private final EventService eventService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public EventResponseV1 getEvent(@PathVariable Long id) {
        Event event = eventService.readEvent(id);
        return EventResponseV1.from(event);
    }
}
