package com.alberdev.study.appointmenthub.api.handlers.validation;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationError(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message,
        String path,
        List<FieldMessage> errors
) {
}
