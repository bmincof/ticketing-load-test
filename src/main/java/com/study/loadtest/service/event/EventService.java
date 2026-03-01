package com.study.loadtest.service.event;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.repository.event.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;

    public Event readEvent(Long id) {
        return eventRepository.findById(id);
    }
}
