package com.alberdev.study.appointmenthub.domain.exceptions;

public class ResourceAlreadyExistsException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
