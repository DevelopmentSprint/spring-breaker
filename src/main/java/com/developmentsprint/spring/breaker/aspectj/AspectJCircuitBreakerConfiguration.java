package com.developmentsprint.spring.breaker.aspectj;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import com.developmentsprint.spring.breaker.annotations.AbstractCircuitBreakerConfiguration;
import com.developmentsprint.spring.breaker.annotations.CircuitBreakerConfigurationSelector;
import com.developmentsprint.spring.breaker.annotations.EnableCircuitBreakers;

/**
 * {@code @Configuration} class that registers the Spring infrastructure beans necessary to enable AspectJ-based annotation-driven circuit breaker management.
 * 
 * @author Todd Orr
 * @since 1.0
 * @see EnableCircuitBreakers
 * @see CircuitBreakerConfigurationSelector
 */
@Configuration
public class AspectJCircuitBreakerConfiguration extends AbstractCircuitBreakerConfiguration {

    public static final String CIRCUIT_BREAKER_ASPECT_BEAN_NAME = "com.developmentsprint.spring.breaker.config.internalCircuitBreakerAspect";

    @Bean(name = CIRCUIT_BREAKER_ASPECT_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AnnotationCircuitBreakerAspect circuitBreakerAspect() {
        AnnotationCircuitBreakerAspect circuitBreakerAspect = AnnotationCircuitBreakerAspect.aspectOf();
        if (this.circuitManager != null) {
            circuitBreakerAspect.setCircuitManager(this.circuitManager);
        }
        return circuitBreakerAspect;
    }
}
