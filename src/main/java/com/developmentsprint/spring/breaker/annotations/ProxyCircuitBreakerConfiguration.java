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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import com.developmentsprint.spring.breaker.interceptor.BeanFactoryCircuitBreakerAttributeSourceAdvisor;
import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttributeSource;
import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerInterceptor;

/**
 * {@code @Configuration} class that registers the Spring infrastructure beans necessary to enable proxy-based annotation-driven circuit breaker management.
 * 
 * @author Todd Orr
 * @since 1.0
 * @see EnableCircuitBreakers
 * @see CircuitBreakerConfigurationSelector
 */
@Configuration
public class ProxyCircuitBreakerConfiguration extends AbstractCircuitBreakerConfiguration {

    public static final String CIRCUIT_BREAKER_ADVISOR_BEAN_NAME = "com.developmentsprint.spring.breaker.config.internalCircuitBreakerAdvisor";

    @Bean(name = CIRCUIT_BREAKER_ADVISOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryCircuitBreakerAttributeSourceAdvisor circuitBreakerAdvisor() {
        BeanFactoryCircuitBreakerAttributeSourceAdvisor advisor = new BeanFactoryCircuitBreakerAttributeSourceAdvisor();
        advisor.setCircuitBreakerOperationSource(circuitBreakerOperationSource());
        advisor.setAdvice(circuitBreakerInterceptor());
        advisor.setOrder(this.enableCircuitBreakers.<Integer> getNumber("order"));
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CircuitBreakerAttributeSource circuitBreakerOperationSource() {
        return new AnnotationCircuitBreakerAttributeSource();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CircuitBreakerInterceptor circuitBreakerInterceptor() {
        CircuitBreakerInterceptor interceptor = new CircuitBreakerInterceptor();
        interceptor.setCircuitBreakerAttributeSource(circuitBreakerOperationSource());
        if (this.circuitManager != null) {
            interceptor.setCircuitManager(this.circuitManager);
        }
        return interceptor;
    }

}
