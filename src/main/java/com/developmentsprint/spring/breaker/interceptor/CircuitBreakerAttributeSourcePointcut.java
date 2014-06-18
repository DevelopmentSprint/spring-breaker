package com.developmentsprint.spring.breaker.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.ObjectUtils;

abstract class CircuitBreakerAttributeSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        CircuitBreakerAttributeSource tas = getCircuitBreakerAttributeSource();
        return (tas == null || tas.getCircuitBreakerAttribute(method, targetClass) != null);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CircuitBreakerAttributeSourcePointcut)) {
            return false;
        }
        CircuitBreakerAttributeSourcePointcut otherPc = (CircuitBreakerAttributeSourcePointcut) other;
        return ObjectUtils.nullSafeEquals(getCircuitBreakerAttributeSource(), otherPc.getCircuitBreakerAttributeSource());
    }

    @Override
    public int hashCode() {
        return CircuitBreakerAttributeSourcePointcut.class.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + getCircuitBreakerAttributeSource();
    }

    /**
     * Obtain the underlying {@link CircuitBreakerAttributeSource} (may be {@code null}). To be implemented by subclasses.
     */
    protected abstract CircuitBreakerAttributeSource getCircuitBreakerAttributeSource();

}