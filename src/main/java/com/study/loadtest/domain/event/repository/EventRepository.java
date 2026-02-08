package com.study.loadtest.domain.event.repository;

import com.study.loadtest.domain.event.model.Event;

import java.util.Optional;

public interface EventRepository {

    Optional<Event> findById(Long id);
}
