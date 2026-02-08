package com.study.loadtest.infra.jpa.event;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class EventJpaRepositoryAdapter implements EventRepository {

    private final EventJpaRepository eventJpaRepository;

    @Override
    public Optional<Event> findById(Long id) {
        return eventJpaRepository.findById(id)
                .map(e -> Event.builder()
                        .id(e.getId())
                        .name(e.getName())
                        .startAt(e.getStartAt())
                        .endAt(e.getEndAt())
                        .totalQuantity(e.getTotalQuantity())
                        .remainingQuantity(e.getRemainingQuantity())
                        .status(e.getStatus())
                        .build());
    }
}
