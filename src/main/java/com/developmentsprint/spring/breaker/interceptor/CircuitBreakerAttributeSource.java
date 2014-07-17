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

import java.lang.reflect.Method;

import com.developmentsprint.spring.breaker.annotations.AnnotationCircuitBreakerAttributeSource;

/**
 * Strategy interface used by {@link CircuitBreakerInterceptor} for metadata retrieval.
 * 
 * <p>
 * Implementations know how to source circuit breaker attributes, whether from configuration, metadata attributes at source level (such as Java 5 annotations),
 * or anywhere else.
 * 
 * @author Todd Orr
 * @since 1.0
 * @see CircuitBreakerInterceptor#setCircuitBreakerAttributeSource
 * @see AnnotationCircuitBreakerAttributeSource
 */
public interface CircuitBreakerAttributeSource {

    /**
     * Return the circuit breaker attribute for the given method, or {@code null} if the method is not wrapped in a circuit breaker.
     * 
     * @param method
     *            the method to introspect
     * @param targetClass
     *            the target class. May be {@code null}, in which case the declaring class of the method must be used.
     * @return CircuitBreakerAttribute the matching circuit breaker attribute, or {@code null} if none found
     */
    CircuitBreakerAttribute getCircuitBreakerAttribute(Method method, Class<?> targetClass);

}