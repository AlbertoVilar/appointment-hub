package com.alberdev.study.appointmenthub.api.handlers.fielderror;

import java.time.LocalDateTime;

public record ApiError(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message,
        String path

        ) {
}
