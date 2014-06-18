package com.developmentsprint.spring.breaker.annotations;

import java.lang.reflect.AnnotatedElement;

import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttribute;

public interface CircuitBreakerAnnotationParser {

    /**
     * Parse the circuit breaker attribute for the given method or class, based on a known annotation type.
     * <p>
     * This essentially parses a known circuit breaker annotation into Spring's metadata attribute class. Returns {@code null} if the method/class is not
     * wrapped in a circuit breaker.
     * 
     * @param ae
     *            the annotated method or class
     * @return CircuitBreakerAttribute the configured circuit breaker attribute, or {@code null} if none was found
     * @see AnnotationCircuitBreakerAttributeSource#determineCircuitBreakerAttribute
     */
    CircuitBreakerAttribute parseCircuitBreakerAnnotation(AnnotatedElement ae);

}
