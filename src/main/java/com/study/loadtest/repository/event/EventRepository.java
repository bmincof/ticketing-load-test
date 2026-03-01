package com.study.loadtest.repository.event;

import com.study.loadtest.domain.event.model.Event;
import lombok.NonNull;

public interface EventRepository {

    @NonNull
    Event findById(Long id);
}
