package com.study.loadtest.domain.event.exception;

import lombok.NonNull;

public class SoldOutException extends RuntimeException {

    public SoldOutException(@NonNull Long id) {
        super(formatMessage(id));
    }

    private static String formatMessage(@NonNull Long id) {
        return String.format("이벤트 재고 소진 (id=%d)", id);
    }
}
