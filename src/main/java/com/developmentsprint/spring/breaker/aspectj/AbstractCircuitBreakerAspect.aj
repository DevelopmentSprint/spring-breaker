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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.MethodSignature;

import com.developmentsprint.spring.breaker.CircuitManager;
import com.developmentsprint.spring.breaker.interceptor.AopAllianceInvoker;
import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttributeSource;
import com.developmentsprint.spring.breaker.support.CircuitBreakerAspectSupport;

/**
 * Abstract superaspect for AspectJ circuit breaker aspects. Concrete subaspects will implement the {@link #circuitBreakerMethodExecution} pointcut using a
 * strategy such as Java 5 annotations.
 * 
 * <p>
 * Suitable for use inside or outside the Spring IoC container. Set the {@link #setCircuitManager circuitManager} property appropriately, allowing use of any
 * circuit manager implementation supported by Spring.
 * 
 * <p>
 * <b>NB:</b> If a method implements an interface that is itself circuit breaker annotated, the relevant Spring circuit breaker definition will <i>not</i> be
 * resolved.
 * 
 * @author Todd Orr
 * @since 1.0
 */
public abstract aspect AbstractCircuitBreakerAspect extends CircuitBreakerAspectSupport {

    protected AbstractCircuitBreakerAspect() {
    }

    /**
     * Construct object using the given circuit breaker metadata retrieval strategy.
     * 
     * @param cbAttributeSource
     *            {@link CircuitBreakerAttributeSource} implementation, retrieving Spring circuit breaker metadata for each joinpoint.
     */
    protected AbstractCircuitBreakerAspect(CircuitBreakerAttributeSource cbAttributeSource) {
        setCircuitBreakerAttributeSource(cbAttributeSource);
    }

    @SuppressAjWarnings("adviceDidNotMatch")
    Object around(final Object circuitBreakerObject) : circuitBreakerMethodExecution(circuitBreakerObject) {

        MethodInvocation invocation = new MethodInvocation() {

            @Override
            public Object proceed() throws Throwable {
                return getMethod().invoke(getThis(), getArguments());
            }

            @Override
            public Object getThis() {
                return thisJoinPoint.getThis();
            }

            @Override
            public AccessibleObject getStaticPart() {
                return getMethod();
            }

            @Override
            public Object[] getArguments() {
                return thisJoinPoint.getArgs();
            }

            @Override
            public Method getMethod() {
                MethodSignature methodSignature = (MethodSignature) thisJoinPoint.getSignature();
                return methodSignature.getMethod();
            }
        };

        CircuitManager.Invoker<?> aspectJInvoker = new AopAllianceInvoker<Object>(invocation, getCircuitBreakerAttributeSource()) {
            @Override
            public Object invoke() {
                return proceed(circuitBreakerObject);
            }
        };

        return super.invokeWithinCircuitBreaker(aspectJInvoker);
    }

    /**
     * Concrete subaspects must implement this pointcut, to identify circuit breaker methods.
     */
    protected abstract pointcut circuitBreakerMethodExecution(Object circuitBreakerObject);

}
