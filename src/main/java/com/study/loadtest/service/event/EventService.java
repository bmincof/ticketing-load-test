package com.study.loadtest.service.event;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.domain.event.repository.EventRepository;
import com.study.loadtest.shared.exception.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;

    public Event readEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException(Event.class, id));
    }
}
