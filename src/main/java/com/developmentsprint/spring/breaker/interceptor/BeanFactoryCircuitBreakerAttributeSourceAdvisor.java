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
