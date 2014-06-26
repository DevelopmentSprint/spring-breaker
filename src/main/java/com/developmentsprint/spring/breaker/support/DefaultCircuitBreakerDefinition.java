/**
 * Copyright 2014 Development Sprint, LLC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.developmentsprint.spring.breaker.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.developmentsprint.spring.breaker.CircuitBreakerDefinition;
import com.developmentsprint.spring.breaker.interceptor.DefaultCircuitBreakerAttribute;

/**
 * Default implementation of the {@link CircuitBreakerDefinition} interface, offering bean-style configuration and sensible default values.
 * 
 * <p>
 * Base class for {@link DefaultCircuitBreakerAttribute}.
 * 
 * @author Todd Orr
 * @since 1.0
 */
@SuppressWarnings("serial")
public class DefaultCircuitBreakerDefinition implements CircuitBreakerDefinition, Serializable {

    private String name;

    private Map<String, String> properties = new HashMap<String, String>();
    
    private String circuitManager;

    /**
     * Create a new {@link DefaultCircuitBreakerDefinition}, with default settings. Can be modified through bean property setters.
     * 
     * @see #setProperties
     * @see #setName
     */
    public DefaultCircuitBreakerDefinition() {
    }

    /**
     * Copy constructor. Definition can be modified through bean property setters.
     * 
     * @see #setProperties
     * @see #setName
     */
    public DefaultCircuitBreakerDefinition(CircuitBreakerDefinition other) {
        this.properties = other.getProperties();
        this.name = other.getName();
    }

    /**
     * Set the name of this circuit breaker. Default is none.
     * <p>
     * This will be used as circuit breaker name to be shown in a circuit breaker monitor, if applicable (for example, Hystrix's).
     */
    public final void setName(String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getCircuitManager() {
        return circuitManager;
    }

    public void setCircuitManager(String circuitManager) {
        this.circuitManager = circuitManager;
    }

    /**
     * This implementation compares the {@code toString()} results.
     * 
     * @see #toString()
     */
    @Override
    public boolean equals(Object other) {
        return (other instanceof CircuitBreakerDefinition && toString().equals(other.toString()));
    }

    /**
     * This implementation returns {@code toString()}'s hash code.
     * 
     * @see #toString()
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Return an identifying description for this circuit breaker definition.
     * <p>
     * Has to be overridden in subclasses for correct {@code equals} and {@code hashCode} behavior. Alternatively, {@link #equals} and {@link #hashCode} can be
     * overridden themselves.
     * 
     * @see #getDefinitionDescription()
     */
    @Override
    public String toString() {
        return getDefinitionDescription().toString();
    }

    /**
     * Return an identifying description for this circuit breaker definition.
     * <p>
     * Available to subclasses, for inclusion in their {@code toString()} result.
     */
    protected final StringBuilder getDefinitionDescription() {
        StringBuilder result = new StringBuilder();
        result.append(name);
        result.append(properties);
        return result;
    }

}
