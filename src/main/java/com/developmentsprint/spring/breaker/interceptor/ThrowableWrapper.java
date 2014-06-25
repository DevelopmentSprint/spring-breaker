package com.developmentsprint.spring.breaker.interceptor;

class ThrowableWrapper extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Throwable original;

    ThrowableWrapper(Throwable original) {
        this.original = original;
    }

    public Throwable getOriginal() {
        return original;
    }
}
