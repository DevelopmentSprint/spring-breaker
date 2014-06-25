package com.developmentsprint.spring.breaker.support;

import com.developmentsprint.spring.breaker.CircuitManager;

/**
 * A basic, no operation {@link CircuitManager} implementation suitable for disabling circuit breaking, typically used for backing circuit breaker declarations
 * without an actual backing circuit manager.
 * 
 * 
 * @author Todd Orr
 * @since 1.0
 * @see CompositeCircuitManager
 */
public class NoOpCircuitManager implements CircuitManager {

    /**
     * Performs no circuit breaking. This method passes through to the actual invocation.
     */
    @Override
    public Object execute(Invoker invoker) {
        return invoker.invoke();
    }

}
