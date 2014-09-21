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

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;

import com.developmentsprint.spring.breaker.CircuitManager;
import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttributeSource;

public abstract class CircuitBreakerAspectSupport implements InitializingBean, ApplicationContextAware, EmbeddedValueResolverAware {

    private CircuitManager circuitManager;

    private boolean initialized = false;

    private CircuitBreakerAttributeSource circuitBreakerAttributeSource;

    private ApplicationContext applicationContext;

    private StringValueResolver valueResolver;

    public CircuitManager getCircuitManager() {
        return circuitManager;
    }

    public void setCircuitManager(CircuitManager circuitManager) {
        this.circuitManager = circuitManager;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.valueResolver = resolver;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
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

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected <T> T invokeWithinCircuitBreaker(CircuitManager.Invoker<T> invoker) {

        // ensure that properties were replaced
        Map<String,String> properties = invoker.getCircuitBreakerAttribute().getProperties();
        for (Map.Entry<String, String> e : properties.entrySet()) {
            String value = valueResolver.resolveStringValue(e.getValue());
            properties.put(e.getKey(), value);
        }

        // check whether aspect is enabled
        // to cope with cases where the AJ is pulled in automatically
        if (!this.initialized) {
            return invoker.invoke();
        }

        return getCircuitManager().execute(invoker);
    }

}
