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

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * Advisor driven by a {@link CircuitBreakerAttributeSource}, used to include a circuit breaker advice bean for methods that are to be wrapped in a circuit
 * breaker.
 * 
 * @author Todd Orr
 * @since 1.0
 */
@SuppressWarnings("serial")
public class BeanFactoryCircuitBreakerAttributeSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private CircuitBreakerAttributeSource circuitBreakerAttributeSource;

    private final CircuitBreakerAttributeSourcePointcut pointcut = new CircuitBreakerAttributeSourcePointcut() {
        @Override
        protected CircuitBreakerAttributeSource getCircuitBreakerAttributeSource() {
            return circuitBreakerAttributeSource;
        }
    };

    /**
     * Set the circuit breaker operation attribute source which is used to find circuit breaker attributes. This should usually be identical to the source
     * reference set on the circuit breaker interceptor itself.
     */
    public void setCircuitBreakerOperationSource(CircuitBreakerAttributeSource circuitBreakerAttributeSource) {
        this.circuitBreakerAttributeSource = circuitBreakerAttributeSource;
    }

    /**
     * Set the {@link ClassFilter} to use for this pointcut. Default is {@link ClassFilter#TRUE}.
     */
    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }

}
