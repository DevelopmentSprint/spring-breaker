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

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.util.Assert;

import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttribute;

public class AnnotationCircuitBreakerAttributeSource extends AbstractFallbackCircuitBreakerAttributeSource implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean publicMethodsOnly;

    private final Set<CircuitBreakerAnnotationParser> annotationParsers;

    /**
     * Create a default AnnotationCircuitBreakerAttributeSource, supporting public methods that carry the {@code CircuitBreaker}.
     */
    public AnnotationCircuitBreakerAttributeSource() {
        this(true);
    }

    /**
     * Create a custom AnnotationCircuitBreakerAttributeSource, supporting public methods that carry the {@code CircuitBreaker} annotation.
     * 
     * @param publicMethodsOnly
     *            whether to support public methods that carry the {@code CircuitBreaker} annotation only (typically for use with proxy-based AOP), or
     *            protected/private methods as well (typically used with AspectJ class weaving)
     */
    public AnnotationCircuitBreakerAttributeSource(boolean publicMethodsOnly) {
        this.publicMethodsOnly = publicMethodsOnly;
        this.annotationParsers = new LinkedHashSet<CircuitBreakerAnnotationParser>(2);
        this.annotationParsers.add(new SpringCircuitBreakerAnnotationParser());
    }

    /**
     * Create a custom AnnotationCircuitBreakerAttributeSource.
     * 
     * @param annotationParser
     *            the CircuitBreakerAnnotationParser to use
     */
    public AnnotationCircuitBreakerAttributeSource(CircuitBreakerAnnotationParser annotationParser) {
        this.publicMethodsOnly = true;
        Assert.notNull(annotationParser, "CircuitBreakerAnnotationParser must not be null");
        this.annotationParsers = Collections.singleton(annotationParser);
    }

    /**
     * Create a custom AnnotationTransactionAttributeSource.
     * 
     * @param annotationParsers
     *            the TransactionAnnotationParsers to use
     */
    public AnnotationCircuitBreakerAttributeSource(CircuitBreakerAnnotationParser... annotationParsers) {
        this.publicMethodsOnly = true;
        Assert.notEmpty(annotationParsers, "At least one CircuitBreakerAnnotationParser needs to be specified");
        Set<CircuitBreakerAnnotationParser> parsers = new LinkedHashSet<CircuitBreakerAnnotationParser>(annotationParsers.length);
        Collections.addAll(parsers, annotationParsers);
        this.annotationParsers = parsers;
    }

    /**
     * Create a custom AnnotationCircuitBreakerAttributeSource.
     * 
     * @param annotationParsers
     *            the CircuitBreakerAnnotationParsers to use
     */
    public AnnotationCircuitBreakerAttributeSource(Set<CircuitBreakerAnnotationParser> annotationParsers) {
        this.publicMethodsOnly = true;
        Assert.notEmpty(annotationParsers, "At least one CircuitBreakerAnnotationParser needs to be specified");
        this.annotationParsers = annotationParsers;
    }

    @Override
    protected CircuitBreakerAttribute findCircuitBreakerAttribute(Method method) {
        return determineCircuitBreakerAttribute(method);
    }

    @Override
    protected CircuitBreakerAttribute findCircuitBreakerAttribute(Class<?> clazz) {
        return determineCircuitBreakerAttribute(clazz);
    }

    /**
     * Determine the circuit breaker attribute for the given method or class.
     * <p>
     * This implementation delegates to configured {@link CircuitBreakerAnnotationParser CircuitBreakerAnnotationParsers} for parsing known annotations into
     * Spring's metadata attribute class. Returns {@code null} if it's not wired for circuit breaking.
     * <p>
     * Can be overridden to support custom annotations that carry transaction metadata.
     * 
     * @param ae
     *            the annotated method or class
     * @return CircuitBreakerAttribute the configured circuit breaker attribute, or {@code null} if none was found
     */
    protected CircuitBreakerAttribute determineCircuitBreakerAttribute(AnnotatedElement ae) {
        for (CircuitBreakerAnnotationParser annotationParser : this.annotationParsers) {
            CircuitBreakerAttribute attr = annotationParser.parseCircuitBreakerAnnotation(ae);
            if (attr != null) {
                return attr;
            }
        }
        return null;
    }

    /**
     * By default, only public methods can be wrapped in a circuit breaker.
     */
    @Override
    protected boolean allowPublicMethodsOnly() {
        return this.publicMethodsOnly;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationCircuitBreakerAttributeSource)) {
            return false;
        }
        AnnotationCircuitBreakerAttributeSource otherTas = (AnnotationCircuitBreakerAttributeSource) other;
        return (this.annotationParsers.equals(otherTas.annotationParsers) && this.publicMethodsOnly == otherTas.publicMethodsOnly);
    }

    @Override
    public int hashCode() {
        return this.annotationParsers.hashCode();
    }

}
