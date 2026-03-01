package com.study.loadtest.repository.event;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.shared.exception.NoSuchEntityException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class EventJpaRepositoryWrapper implements EventRepository {

    private final EventJpaRepository eventJpaRepository;

    @NonNull
    @Override
    public Event findById(Long id) {
        return eventJpaRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException(Event.class, id));
    }
}
