package com.developmentsprint.spring.breaker;

import java.lang.reflect.Method;

import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttribute;

public interface CircuitManager {

    Object execute(Invoker invoker);

    public interface Invoker {

        CircuitBreakerAttribute getCircuitBreakerAttribute();

        Object invoke();

        Object getTarget();

        Class<?> getTargetClass();

        Method getMethod();

        Object[] getArguments();

    }

}
