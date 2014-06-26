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
import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.ObjectUtils;

abstract class CircuitBreakerAttributeSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        CircuitBreakerAttributeSource tas = getCircuitBreakerAttributeSource();
        return (tas == null || tas.getCircuitBreakerAttribute(method, targetClass) != null);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CircuitBreakerAttributeSourcePointcut)) {
            return false;
        }
        CircuitBreakerAttributeSourcePointcut otherPc = (CircuitBreakerAttributeSourcePointcut) other;
        return ObjectUtils.nullSafeEquals(getCircuitBreakerAttributeSource(), otherPc.getCircuitBreakerAttributeSource());
    }

    @Override
    public int hashCode() {
        return CircuitBreakerAttributeSourcePointcut.class.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + getCircuitBreakerAttributeSource();
    }

    /**
     * Obtain the underlying {@link CircuitBreakerAttributeSource} (may be {@code null}). To be implemented by subclasses.
     */
    protected abstract CircuitBreakerAttributeSource getCircuitBreakerAttributeSource();

}