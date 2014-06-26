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
