package com.developmentsprint.spring.breaker.interceptor;

import java.util.Map;

import com.developmentsprint.spring.breaker.CircuitBreakerDefinition;

public interface CircuitBreakerAttribute extends CircuitBreakerDefinition {

    String getName();

    Map<String, String> getProperties();

}
