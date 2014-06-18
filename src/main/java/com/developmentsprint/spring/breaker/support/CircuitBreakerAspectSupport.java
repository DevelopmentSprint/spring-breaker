package com.developmentsprint.spring.breaker.support;

import org.springframework.beans.factory.InitializingBean;

import com.developmentsprint.spring.breaker.CircuitManager;
import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttributeSource;

public abstract class CircuitBreakerAspectSupport implements InitializingBean {

    private CircuitManager circuitManager;

    private boolean initialized = false;

    private CircuitBreakerAttributeSource circuitBreakerAttributeSource;

    public CircuitManager getCircuitManager() {
        return circuitManager;
    }

    public void setCircuitManager(CircuitManager circuitManager) {
        this.circuitManager = circuitManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO Auto-generated method stub
        initialized = true;
    }

    public void setCircuitBreakerAttributeSource(CircuitBreakerAttributeSource circuitBreakerAttributeSource) {
        this.circuitBreakerAttributeSource = circuitBreakerAttributeSource;
    }

    /**
     * Return the circuit breaker attribute source.
     */
    public CircuitBreakerAttributeSource getCircuitBreakerAttributeSource() {
        return this.circuitBreakerAttributeSource;
    }

    protected Object invokeWithinCircuitBreaker(CircuitManager.Invoker invoker) {
        // check whether aspect is enabled
        // to cope with cases where the AJ is pulled in automatically
        if (!this.initialized) {
            return invoker.invoke();
        }

        return getCircuitManager().execute(invoker);
    }

}
