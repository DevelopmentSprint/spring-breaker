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
package com.developmentsprint.spring.breaker;

import java.util.Map;

import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttribute;
import com.developmentsprint.spring.breaker.support.CircuitBreakerAspectSupport;
import com.developmentsprint.spring.breaker.support.DefaultCircuitBreakerDefinition;

/**
 * Interface that defines Spring-compliant circuit breaker properties.
 * 
 * @author Todd Orr
 * @since 1.0
 * @see DefaultCircuitBreakerDefinition
 * @see CircuitBreakerAttribute
 */
public interface CircuitBreakerDefinition {

    /**
     * Return the name of this circuit breaker. Cannot be {@code null}.
     * <p>
     * This will be used as the circuit breaker name to be shown in a circuit breaker monitor, if applicable (for example, Hystrix's).
     * <p>
     * In case of Spring's declarative circuit breakers, the exposed name will be the {@code fully-qualified class name + "." + method name} (by default).
     * 
     * @return the name of this circuit breaker.
     * @see CircuitBreakerAspectSupport
     */
    String getName();

    /**
     * Return the custom properties that will be passed to the specific {@link CircuitManager} implementation.
     * 
     * @return the circuit breaker's properties
     * @see CircuitManager#execute(com.developmentsprint.spring.breaker.CircuitManager.Invoker)
     */
    Map<String, String> getProperties();

    /**
     * @return The circuit manager bean name associated with this circuit breaker. By default this is the circuitManager associated with the advice,
     *         annotation-config, or "circuitManager" if otherwise undefined.
     */
    String getCircuitManager();

}
