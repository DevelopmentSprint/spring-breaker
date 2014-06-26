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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttribute;
import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttributeSource;
import com.developmentsprint.spring.breaker.interceptor.DefaultCircuitBreakerAttribute;

/**
 * Abstract implementation of {@link CircuitBreakerAttributeSource} that caches attributes for methods and implements a fallback policy:
 * <ol>
 * <li>specific target method
 * <li>target class
 * <li>declaring method
 * <li>declaring class/interface.
 * </ol>
 * 
 * <p>
 * Defaults to using the target class's circuit breaker attribute if none is associated with the target method. Any circuit breaker attribute associated with
 * the target method completely overrides a class circuit breaker attribute. If none found on the target class, the interface that the invoked method has been
 * called through (in case of a JDK proxy) will be checked.
 * 
 * <p>
 * This implementation caches attributes by method after they are first used. If it is ever desirable to allow dynamic changing of circuit breaker attributes
 * (which is very unlikely), caching could be made configurable.
 * 
 * @author Todd Orr
 * @since 1.0
 */
abstract class AbstractFallbackCircuitBreakerAttributeSource implements CircuitBreakerAttributeSource {

    /**
     * Canonical value held in cache to indicate no circuit breaker attribute was found for this method, and we don't need to look again.
     */
    private final static CircuitBreakerAttribute NULL_CIRCUIT_BREAKER_ATTRIBUTE = new DefaultCircuitBreakerAttribute();

    /**
     * Logger available to subclasses.
     * <p>
     * As this base class is not marked Serializable, the logger will be recreated after serialization - provided that the concrete subclass is Serializable.
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Cache of {@link CircuitBreakerAttribute}s, keyed by DefaultCacheKey (Method + target Class).
     * <p>
     * As this base class is not marked Serializable, the cache will be recreated after serialization - provided that the concrete subclass is Serializable.
     */
    final Map<Object, CircuitBreakerAttribute> attributeCache = new ConcurrentHashMap<Object, CircuitBreakerAttribute>(1024);

    /**
     * Determine the circuit breaker attribute for this method invocation.
     * <p>
     * Defaults to the class's circuit breaker attribute if no method attribute is found.
     * 
     * @param method
     *            the method for the current invocation (never {@code null})
     * @param targetClass
     *            the target class for this invocation (may be {@code null})
     * @return CircuitBreakerAttribute for this method, or {@code null} if the method is not wrapped in a circuit breaker
     */
    public CircuitBreakerAttribute getCircuitBreakerAttribute(Method method, Class<?> targetClass) {
        // First, see if we have a cached value.
        Object cacheKey = getCacheKey(method, targetClass);
        Object cached = this.attributeCache.get(cacheKey);
        if (cached != null) {
            // Value will either be canonical value indicating there is no circuit breaker attribute,
            // or an actual circuit breaker attribute.
            if (cached == NULL_CIRCUIT_BREAKER_ATTRIBUTE) {
                return null;
            }
            else {
                return (CircuitBreakerAttribute) cached;
            }
        }
        else {
            // We need to work it out.
            CircuitBreakerAttribute cbAttribute = computeCircuitBreakerAttribute(method, targetClass);
            // Put it in the cache.
            if (cbAttribute == null) {
                this.attributeCache.put(cacheKey, NULL_CIRCUIT_BREAKER_ATTRIBUTE);
            }
            else {
                if (log.isDebugEnabled()) {
                    Class<?> classToLog = (targetClass != null ? targetClass : method.getDeclaringClass());
                    log.debug("Adding circuit breaker method '{}.{}' with attribute: {}", classToLog.getSimpleName(), method.getName(), cbAttribute);
                }
                this.attributeCache.put(cacheKey, cbAttribute);
            }
            return cbAttribute;
        }
    }

    /**
     * Determine a cache key for the given method and target class.
     * <p>
     * Must not produce same key for overloaded methods. Must produce same key for different instances of the same method.
     * 
     * @param method
     *            the method (never {@code null})
     * @param targetClass
     *            the target class (may be {@code null})
     * @return the cache key (never {@code null})
     */
    protected Object getCacheKey(Method method, Class<?> targetClass) {
        return new DefaultCacheKey(method, targetClass);
    }

    /**
     * Same signature as {@link #getCircuitBreakerAttribute}, but doesn't cache the result. {@link #getCircuitBreakerAttribute} is effectively a caching
     * decorator for this method.
     * 
     * @see #getCircuitBreakerAttribute(Method, Class)
     */
    private CircuitBreakerAttribute computeCircuitBreakerAttribute(Method method, Class<?> targetClass) {
        // Don't allow no-public methods as required.
        if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        // Ignore CGLIB subclasses - introspect the actual user class.
        Class<?> userClass = ClassUtils.getUserClass(targetClass);

        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);

        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        // First try is the method in the target class.
        CircuitBreakerAttribute cbAttribute = findCircuitBreakerAttribute(specificMethod);
        if (cbAttribute != null) {
            return cbAttribute;
        }

        // Second try is the circuit breaker attribute on the target class.
        cbAttribute = findCircuitBreakerAttribute(specificMethod.getDeclaringClass());
        if (cbAttribute != null) {
            return cbAttribute;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method.
            cbAttribute = findCircuitBreakerAttribute(method);
            if (cbAttribute != null) {
                return cbAttribute;
            }
            // Last fallback is the class of the original method.
            return findCircuitBreakerAttribute(method.getDeclaringClass());
        }
        return null;
    }

    /**
     * Subclasses need to implement this to return the circuit breaker attribute for the given method, if any.
     * 
     * @param method
     *            the method to retrieve the attribute for
     * @return all circuit breaker attributes associated with this method (or {@code null} if none)
     */
    protected abstract CircuitBreakerAttribute findCircuitBreakerAttribute(Method method);

    /**
     * Subclasses need to implement this to return the circuit breaker attribute for the given class, if any.
     * 
     * @param clazz
     *            the class to retrieve the attribute for
     * @return all circuit breaker attributes associated with this class (or {@code null} if none)
     */
    protected abstract CircuitBreakerAttribute findCircuitBreakerAttribute(Class<?> clazz);

    /**
     * Should only public methods be allowed to have circuit breaker wrappers?
     * <p>
     * The default implementation returns {@code false}.
     */
    protected boolean allowPublicMethodsOnly() {
        return false;
    }

    /**
     * Default cache key for the CircuitBreakerAttribute cache.
     */
    private static class DefaultCacheKey {

        private final Method method;

        private final Class<?> targetClass;

        public DefaultCacheKey(Method method, Class<?> targetClass) {
            this.method = method;
            this.targetClass = targetClass;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof DefaultCacheKey)) {
                return false;
            }
            DefaultCacheKey otherKey = (DefaultCacheKey) other;
            return (this.method.equals(otherKey.method) && ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass));
        }

        @Override
        public int hashCode() {
            return this.method.hashCode();
        }
    }

}
