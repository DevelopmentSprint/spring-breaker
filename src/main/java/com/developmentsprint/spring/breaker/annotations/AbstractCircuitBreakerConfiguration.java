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
package com.developmentsprint.spring.breaker.annotations;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.developmentsprint.spring.breaker.CircuitManager;

/**
 * Abstract base {@code @Configuration} class providing common structure for enabling Spring's annotation-driven circuit breaker management capability.
 * 
 * @author Todd Orr
 * @since 1.0
 * @see EnableCircuitBreakers
 */
@Configuration
public abstract class AbstractCircuitBreakerConfiguration implements ImportAware {

    protected AnnotationAttributes enableCircuitBreakers;

    protected CircuitManager circuitManager;

    @Autowired(required = false)
    private Collection<CircuitBreakerConfigurer> circuitBreakerConfigurers;

    @Autowired(required = false)
    private Collection<CircuitManager> circuitManagerBeans;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableCircuitBreakers = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableCircuitBreakers.class.getName(), false));
        Assert.notNull(this.enableCircuitBreakers, "@EnableCircuitBreakers is not present on importing class " + importMetadata.getClassName());
    }

    /**
     * Determine which {@code CircuitManager} bean to use. Prefer the result of {@link CircuitBreakerConfigurer#circuitManager()} over any by-type matching. If
     * none, fall back to by-type matching on {@code CircuitManager}.
     * 
     * @throws IllegalArgumentException
     *             if no CircuitManager can be found; if more than one CircuitBreakerConfigurer implementation exists; if multiple CircuitManager beans and no
     *             CircuitBreakerConfigurer exists to disambiguate.
     */
    @PostConstruct
    protected void reconcileCircuitManager() {

        if (!CollectionUtils.isEmpty(circuitBreakerConfigurers)) {
            int nConfigurers = circuitBreakerConfigurers.size();
            if (nConfigurers > 1) {
                throw new IllegalStateException(nConfigurers + " implementations of " +
                        "CircuitBreakerConfigurer were found when only 1 was expected. " +
                        "Refactor the configuration such that CircuitBreakerConfigurer is " +
                        "implemented only once or not at all.");
            }
            CircuitBreakerConfigurer circuitBreakerConfigurer = circuitBreakerConfigurers.iterator().next();
            this.circuitManager = circuitBreakerConfigurer.circuitManager();

        } else if (!CollectionUtils.isEmpty(circuitManagerBeans)) {
            int nManagers = circuitManagerBeans.size();
            if (nManagers > 1) {
                throw new IllegalStateException(
                        nManagers
                                + " beans of type CircuitManager were found when only 1 was expected. Remove all but one of the CircuitManager bean definitions, or implement CircuitBreakerConfigurer to make explicit which CircuitManager should be used for annotation-driven circuit breaker management.");
            }
            CircuitManager circuitManager = circuitManagerBeans.iterator().next();
            this.circuitManager = circuitManager;

        } else {
            throw new IllegalStateException(
                    "No bean of type CircuitManager could be found. Register a CircuitManager bean or remove the @EnableCircuitBreakers annotation from your configuration.");
        }
    }
}
