package com.study.loadtest.interfaces.event.v1.response;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.domain.event.model.EventStatus;
import com.study.loadtest.shared.util.DateUtil;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EventResponseV1 {

    private Long id;
    private String name;
    private String startAt;
    private String endAt;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private EventStatus status;

    public static EventResponseV1 from(Event event) {
        return EventResponseV1.builder()
                .id(event.getId())
                .name(event.getName())
                .startAt(DateUtil.toIsoZonedString(event.getStartAt()))
                .endAt(DateUtil.toIsoZonedString(event.getEndAt()))
                .totalQuantity(event.getTotalQuantity())
                .remainingQuantity(event.getRemainingQuantity())
                .status(event.getStatus())
                .build();
    }

}
