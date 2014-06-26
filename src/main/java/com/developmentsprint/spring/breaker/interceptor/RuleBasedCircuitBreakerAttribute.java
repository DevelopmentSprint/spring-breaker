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
