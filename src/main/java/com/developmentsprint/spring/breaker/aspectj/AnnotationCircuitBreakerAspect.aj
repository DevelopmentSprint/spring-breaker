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
package com.developmentsprint.spring.breaker.aspectj;

import com.developmentsprint.spring.breaker.annotations.AnnotationCircuitBreakerAttributeSource;
import com.developmentsprint.spring.breaker.annotations.CircuitBreaker;

/**
 * Concrete AspectJ circuit breaker aspect using Spring's @{@link CircuitBreaker} annotation.
 * 
 * <p>
 * When using this aspect, you <i>must</i> annotate the implementation class (and/or methods within that class), <i>not</i> the interface (if any) that the
 * class implements. AspectJ follows Java's rule that annotations on interfaces are <i>not</i> inherited.
 * 
 * <p>
 * A {@code @CircuitBreaker} annotation on a class specifies the default circuit breaker semantics for the execution of any <b>public</b> operation in the
 * class.
 * 
 * <p>
 * A {@code @CircuitBreaker} annotation on a method within the class overrides the default circuit breaker semantics given by the class annotation (if present).
 * Any method may be annotated (regardless of visibility). Annotating non-public methods directly is the only way to get circuit breaker demarcation for the
 * execution of such operations.
 * 
 * @author Todd Orr
 * @since 1.0
 */
public aspect AnnotationCircuitBreakerAspect extends AbstractCircuitBreakerAspect {

    public AnnotationCircuitBreakerAspect() {
        super(new AnnotationCircuitBreakerAttributeSource(false));
    }

    /**
     * Matches the execution of any method with the @{@link CircuitBreaker} annotation.
     */
    private pointcut executionOfCircuitBreakerMethod() : execution(@CircuitBreaker * *(..));

    /**
     * Definition of pointcut from super aspect - matched join points will have Spring circuit breaker management applied.
     */
    protected pointcut circuitBreakerMethodExecution(Object circuitBreakerObject) : executionOfCircuitBreakerMethod() && this(circuitBreakerObject);

}