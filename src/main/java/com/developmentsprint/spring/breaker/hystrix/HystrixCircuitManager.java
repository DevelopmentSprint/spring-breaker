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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.developmentsprint.spring.breaker.CircuitBreakerException;
import com.developmentsprint.spring.breaker.CircuitManager;
import com.developmentsprint.spring.breaker.CircuitOverloadException;
import com.developmentsprint.spring.breaker.CircuitTimeoutException;
import com.developmentsprint.spring.breaker.hystrix.fallback.FailFastFallback;
import com.developmentsprint.spring.breaker.hystrix.fallback.FailSilentFallback;
import com.developmentsprint.spring.breaker.hystrix.fallback.HystrixFallback;
import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttribute;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.exception.HystrixRuntimeException;

public class HystrixCircuitManager implements CircuitManager, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(HystrixCircuitManager.class);

    private static final String INSTANCE_COMMAND_PROP_KEY_FORMAT = "hystrix.command.%s.%s";

    private static final String INSTANCE_THREADPOOL_PROP_KEY_FORMAT = "hystrix.threadpool.%s.%s";

    private static final String INSTANCE_COLLAPSER_PROP_KEY_FORMAT = "hystrix.collapser.%s.%s";

    private static final Map<String, Boolean> CONFIGURED_BREAKERS = new ConcurrentHashMap<String, Boolean>();

    private Configuration configuration;

    private Properties properties;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (properties != null) {
            ConfigurationManager.loadProperties(properties);
        }
        configuration = ConfigurationManager.getConfigInstance();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Object execute(final Invoker invoker) {

        CircuitBreakerAttribute attr = invoker.getCircuitBreakerAttribute();

        String circuitBreakerName = determineCommandName(attr);
        String circuitBreakerGroup = determineGroupName(attr);
        String threadPoolName = determineThreadPoolName(attr);

        if (!CONFIGURED_BREAKERS.containsKey(circuitBreakerName)) {
            synchronized (circuitBreakerName) {
                if (!CONFIGURED_BREAKERS.containsKey(circuitBreakerName)) {
                    for (Map.Entry<String, String> entry : attr.getProperties().entrySet()) {
                        String commandKey = String.format(INSTANCE_COMMAND_PROP_KEY_FORMAT, circuitBreakerName, entry.getKey());
                        configuration.setProperty(commandKey, entry.getValue());
                        String threadPoolKey = String.format(INSTANCE_THREADPOOL_PROP_KEY_FORMAT, threadPoolName, entry.getKey());
                        configuration.setProperty(threadPoolKey, entry.getValue());
                        String collapserKey = String.format(INSTANCE_COLLAPSER_PROP_KEY_FORMAT, threadPoolName, entry.getKey());
                        configuration.setProperty(collapserKey, entry.getValue());
                    }
                    CONFIGURED_BREAKERS.put(circuitBreakerName, Boolean.TRUE);
                }
            }
        }

        if (log.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            Iterator<String> keyIterator = configuration.getKeys();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                builder.append(System.getProperty("line.separator"))
                    .append("\t")
                    .append(key)
                    .append(" : ")
                    .append(configuration.getString(key));
            }
            log.debug("Configured Hystrix Properties: {}", builder);
        }

        final HystrixFallback<?> fallback = determineFallback(attr);

        log.debug("Creating circuit breaker command '{}' around {}", circuitBreakerName, new Object() {
            public String toString() {
                return invoker.getMethod().toString();
            }
        });

        HystrixCommand.Setter setter = HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(circuitBreakerGroup))
                .andCommandKey(HystrixCommandKey.Factory.asKey(circuitBreakerName))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(threadPoolName));

        HystrixCommand<Object> command = new HystrixCommand<Object>(setter) {

            @Override
            protected Object run() throws Exception {
                try {
                    return invoker.invoke();
                } catch (Exception e) {
                    if (e.getCause() != null && e.getCause() instanceof Exception) {
                        e = ((Exception)e.getCause());
                    }
                    throw e;
                }
            }

            @Override
            protected Object getFallback() {
                if (fallback == null || fallback instanceof FailFastFallback) {
                    return super.getFallback();
                } else if (fallback instanceof FailSilentFallback) {
                    return null;
                } else {
                    return fallback.fallback();
                }
            }
        };

        try {
            return command.execute();
        } catch (HystrixRuntimeException e) {
            Throwable t = e.getCause();
            if (t instanceof CircuitBreakerException) {
                throw (CircuitBreakerException) t.getCause();
            } else if (t instanceof TimeoutException) {
                throw new CircuitTimeoutException(t.getMessage(), t);
            } else if (t instanceof RejectedExecutionException) {
                throw new CircuitOverloadException(t.getMessage(), t);
            }
            throw new CircuitBreakerException(t);
        }
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
