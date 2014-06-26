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

import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

public class CircuitBreakerAttributeSourceAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 1L;

    private CircuitBreakerInterceptor circuitBreakerInterceptor;

    private final CircuitBreakerAttributeSourcePointcut pointcut = new CircuitBreakerAttributeSourcePointcut() {

        private static final long serialVersionUID = 1L;

        @Override
        protected CircuitBreakerAttributeSource getCircuitBreakerAttributeSource() {
            return (circuitBreakerInterceptor != null ? circuitBreakerInterceptor.getCircuitBreakerAttributeSource() : null);
        }
    };

    /**
     * Create a new CircuitBreakerAttributeSourceAdvisor.
     */
    public CircuitBreakerAttributeSourceAdvisor() {
        // default
    }

    /**
     * Create a new CircuitBreakerAttributeSourceAdvisor.
     * 
     * @param interceptor
     *            the circuit breaker interceptor to use for this advisor
     */
    public CircuitBreakerAttributeSourceAdvisor(CircuitBreakerInterceptor interceptor) {
        setCircuitBreakerInterceptor(interceptor);
    }

    /**
     * Set the circuit breaker interceptor to use for this advisor.
     */
    public void setCircuitBreakerInterceptor(CircuitBreakerInterceptor interceptor) {
        this.circuitBreakerInterceptor = interceptor;
    }

    /**
     * Set the {@link ClassFilter} to use for this pointcut. Default is {@link ClassFilter#TRUE}.
     */
    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    public Advice getAdvice() {
        return this.circuitBreakerInterceptor;
    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }

}
