package com.study.loadtest.shared.exception;

import lombok.NonNull;

public class InvalidStateException extends RuntimeException {

    public <T> InvalidStateException(@NonNull Class<T> model, @NonNull Long id, @NonNull String state) {
        super(formatMessage(model.getSimpleName(), id, state));
    }

    private static String formatMessage(@NonNull String entityName, @NonNull Long id, @NonNull String state) {
        return String.format("엔티티[%s] 유효하지 않은 상태 (id=%d, state=%s)", entityName, id, state);
    }
}
