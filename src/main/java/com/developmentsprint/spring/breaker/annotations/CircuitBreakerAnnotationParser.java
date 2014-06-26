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
