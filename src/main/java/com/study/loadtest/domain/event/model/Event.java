package com.study.loadtest.domain.event.model;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Builder
@Getter
public class Event {

    private Long id;
    private String name;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private EventStatus status;
}
