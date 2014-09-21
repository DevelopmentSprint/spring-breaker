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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.developmentsprint.spring.breaker.CircuitBreakerException;
import com.developmentsprint.spring.breaker.CircuitManager;
import com.developmentsprint.spring.breaker.support.CircuitBreakerAspectSupport;

/**
 * AOP Alliance MethodInterceptor for declarative circuit breaker management using the common Spring circuit breaker infrastructure.
 * 
 * <p>
 * Derives from the {@link CircuitBreakerAspectSupport} class which contains the integration with Spring's underlying circuit breaker API.
 * CircuitBreakerInterceptor simply calls the relevant superclass methods in the correct order.
 * 
 * <p>
 * CircuitBreakerInterceptor are thread-safe.
 * 
 * @author Todd Orr
 * @since 1.0
 */
public class CircuitBreakerInterceptor extends CircuitBreakerAspectSupport implements MethodInterceptor, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        CircuitManager.Invoker<?> aopAllianceInvoker = new AopAllianceInvoker<Object>(invocation, getCircuitBreakerAttributeSource());

        try {
            return invokeWithinCircuitBreaker(aopAllianceInvoker);
        } catch (CircuitBreakerException e) {
            throw e;
        } catch (Throwable e) {
            throw new CircuitBreakerException(e.getMessage(), e);
        }
    }

}
