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
package com.developmentsprint.spring.breaker;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Future;

import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttribute;

public interface CircuitManager {

    <T> T execute(Invoker<T> invoker);

    <T> Future<T> queue(Invoker<T> invoker);

    List<CircuitBreakerDefinition> getConfiguredCircuitBreakers();

    public interface Invoker<T> {

        CircuitBreakerAttribute getCircuitBreakerAttribute();

        T invoke();

        Object getTarget();

        Class<?> getTargetClass();

        Method getMethod();

        Object[] getArguments();

    }

}
