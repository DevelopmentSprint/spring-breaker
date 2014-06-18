package com.developmentsprint.spring.breaker.interceptor;

import com.developmentsprint.spring.breaker.support.DefaultCircuitBreakerDefinition;

public class DefaultCircuitBreakerAttribute extends DefaultCircuitBreakerDefinition implements CircuitBreakerAttribute {

    private static final long serialVersionUID = 1L;

    private String qualifier;

    /**
     * Create a new {@link DefaultCircuitBreakerAttribute}, with default settings. Can be modified through bean property setters.
     * 
     */
    public DefaultCircuitBreakerAttribute() {
        super();
    }

    /**
     * Copy constructor. Definition can be modified through bean property setters.
     * 
     */
    public DefaultCircuitBreakerAttribute(CircuitBreakerAttribute other) {
        super(other);
    }

    /**
     * Associate a qualifier value with this circuit breaker attribute.
     * <p>
     * This may be used for choosing a corresponding circuit manager to process this specific circuit breaker.
     */
    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    /**
     * Return a qualifier value associated with this circuit breaker attribute.
     */
    public String getQualifier() {
        return this.qualifier;
    }

    /**
     * Return an identifying description for this circuit breaker attribute.
     * <p>
     * Available to subclasses, for inclusion in their {@code toString()} result.
     */
    protected final StringBuilder getAttributeDescription() {
        StringBuilder result = getDefinitionDescription();
        if (this.qualifier != null) {
            result.append("; '").append(this.qualifier).append("'");
        }
        return result;
    }

}
