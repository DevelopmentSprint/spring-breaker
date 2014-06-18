package com.developmentsprint.spring.breaker;

import org.springframework.core.NestedRuntimeException;

public class CircuitBreakerException extends NestedRuntimeException {

    private static final long serialVersionUID = 1L;

    public CircuitBreakerException(String msg) {
        super(msg);
    }

    public CircuitBreakerException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
