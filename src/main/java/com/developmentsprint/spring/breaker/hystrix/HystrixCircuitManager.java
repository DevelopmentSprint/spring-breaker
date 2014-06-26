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
package com.developmentsprint.spring.breaker.hystrix;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.developmentsprint.spring.breaker.CircuitBreakerException;
import com.developmentsprint.spring.breaker.CircuitManager;
import com.developmentsprint.spring.breaker.hystrix.fallback.FailFastFallback;
import com.developmentsprint.spring.breaker.hystrix.fallback.FailSilentFallback;
import com.developmentsprint.spring.breaker.hystrix.fallback.HystrixFallback;
import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttribute;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.netflix.hystrix.HystrixThreadPoolKey;

public class HystrixCircuitManager implements CircuitManager {

    private static final Logger log = LoggerFactory.getLogger(HystrixCircuitManager.class);

    @Override
    public Object execute(final Invoker invoker) {

        CircuitBreakerAttribute attr = invoker.getCircuitBreakerAttribute();

        String circuitBreakerName = determineCommandName(attr);

        String circuitBreakerGroup = determineGroupName(attr);

        String threadPoolName = determineThreadPoolName(attr);

        ExecutionIsolationStrategy isolationStrategy = determineIsolationStrategy(attr);

        final HystrixFallback<?> fallback = determineFallback(attr);

        log.debug("Creating circuit breaker command '{}' around {}", circuitBreakerName, new Object() {
            public String toString() {
                return invoker.getMethod().toString();
            }
        });

        HystrixCommand.Setter setter = HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(circuitBreakerGroup))
                .andCommandKey(HystrixCommandKey.Factory.asKey(circuitBreakerName))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(isolationStrategy))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(threadPoolName));

        HystrixCommand<Object> command = new HystrixCommand<Object>(setter) {

            @Override
            protected Object run() throws Exception {
                try {
                    return invoker.invoke();
                } catch (Exception e) {
                    throw e;
                } catch (Throwable e) {
                    throw new Exception(e);
                }
            }

            @Override
            protected Object getFallback() {
                if (fallback instanceof FailFastFallback) {
                    return super.getFallback();
                } else if (fallback instanceof FailSilentFallback) {
                    return null;
                } else {
                    return fallback.fallback();
                }
            }
        };

        return command.execute();
    }

    private String determineGroupName(CircuitBreakerAttribute attr) {
        Map<String, String> properties = attr.getProperties();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().equals("groupKey")) {
                return entry.getValue();
            }
        }
        return attr.getName();
    }

    private String determineThreadPoolName(CircuitBreakerAttribute attr) {
        Map<String, String> properties = attr.getProperties();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().equals("threadPoolName")) {
                return entry.getValue();
            }
        }
        return attr.getName();
    }

    private String determineCommandName(CircuitBreakerAttribute attr) {
        return attr.getName();
    }

    private ExecutionIsolationStrategy determineIsolationStrategy(CircuitBreakerAttribute attr) {
        Map<String, String> properties = attr.getProperties();
        String strategyName = ExecutionIsolationStrategy.THREAD.name();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().equals("executionIsolationStrategy")) {
                strategyName = entry.getValue();
            }
        }
        ExecutionIsolationStrategy strategy = ExecutionIsolationStrategy.valueOf(strategyName);
        return strategy;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private HystrixFallback<?> determineFallback(CircuitBreakerAttribute attr) {
        Map<String, String> properties = attr.getProperties();
        String fallbackClassName = null;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().equals("fallbackClass")) {
                fallbackClassName = entry.getValue();
            }
        }

        if (StringUtils.isBlank(fallbackClassName)) {
            return null;
        }

        Class<? extends HystrixFallback> fallbackClass;
        try {
            fallbackClass = (Class<? extends HystrixFallback>) Class.forName(fallbackClassName);
        } catch (ClassNotFoundException e) {
            throw new CircuitBreakerException(e.getMessage(), e);
        }

        try {
            fallbackClass.getDeclaredConstructor().setAccessible(true);
            return fallbackClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new CircuitBreakerException(e.getMessage(), e);
        }
    }

}
