package com.developmentsprint.spring.breaker.interceptor;

import java.io.Serializable;

/**
 * {@link CircuitBreakerAttribute} implementation that behaves like DefaultCircuitBreakerAttribute.
 * 
 * @author Todd Orr
 * @since 1.0
 */
@SuppressWarnings("serial")
public class RuleBasedCircuitBreakerAttribute extends DefaultCircuitBreakerAttribute implements Serializable {

    private String methodName;

    /**
     * Create a new RuleBasedCircuitBreakerAttribute, with default settings. Can be modified through bean property setters.
     */
    public RuleBasedCircuitBreakerAttribute() {
        super();
    }

    /**
     * Copy constructor. Definition can be modified through bean property setters.
     */
    public RuleBasedCircuitBreakerAttribute(RuleBasedCircuitBreakerAttribute other) {
        super(other);
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return getAttributeDescription().append(",").append(methodName).toString();
    }

}
