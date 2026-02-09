package com.study.loadtest.shared.exception;

import lombok.NonNull;

public class NoSuchEntityException extends RuntimeException {

    public <T> NoSuchEntityException(@NonNull Class<T> model, @NonNull Long id) {
        super(formatMessage(model.getSimpleName(), id));
    }

    private static String formatMessage(@NonNull String entityName, @NonNull Long id) {
        return String.format("엔티티[%s] 찾을 수 없음 (id=%d)", entityName, id);
    }
}
