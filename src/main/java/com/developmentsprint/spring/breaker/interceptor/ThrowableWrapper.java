package com.developmentsprint.spring.breaker.interceptor;

import lombok.Getter;

class ThrowableWrapper extends RuntimeException {

    private static final long serialVersionUID = 1L;

    @Getter
    private final Throwable original;

    ThrowableWrapper(Throwable original) {
        this.original = original;
    }
}
