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
package com.developmentsprint.spring.breaker.support;

import java.util.List;
import java.util.concurrent.Future;

import com.developmentsprint.spring.breaker.CircuitBreakerDefinition;
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
    public <T> T execute(Invoker<T> invoker) {
        return invoker.invoke();
    }

    /**
     * Performs no circuit breaking. This method passes through to the actual invocation.
     */
    @Override
    public <T> Future<T> queue(Invoker<T> invoker) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<CircuitBreakerDefinition> getConfiguredCircuitBreakers() {
        // TODO Auto-generated method stub
        return null;
    }

}
