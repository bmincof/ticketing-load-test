package com.study.loadtest.repository.event;

import com.study.loadtest.domain.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<Event, Long> {
}
