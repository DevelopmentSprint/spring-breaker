package com.developmentsprint.spring.breaker.annotations;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;

/**
 * Selects which implementation of {@link AbstractCircuitBreakerConfiguration} should be used based on the value of {@link EnableCircuitBreakers#mode} on the
 * importing {@code @Configuration} class.
 * 
 * @author Todd Orr
 * @since 1.0
 * @see EnableCircuitBreakers
 * @see ProxyCircuitBreakerConfiguration
 */
public class CircuitBreakerConfigurationSelector extends AdviceModeImportSelector<EnableCircuitBreakers> {

    private static final String CIRCUIT_BREAKER_ASPECT_CONFIGURATION_CLASS_NAME = "com.developmentsprint.spring.breaker.aspectj.AspectJCircuitBreakerConfiguration";

    /**
     * {@inheritDoc}
     * 
     * @return {@link ProxyCircuitBreakerConfiguration} or {@code AspectJCircuitBreakerConfiguration} for {@code PROXY} and {@code ASPECTJ} values of
     *         {@link EnableCircuitBreakers#mode()}, respectively
     */
    @Override
    public String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
        case PROXY:
            return new String[] { AutoProxyRegistrar.class.getName(), ProxyCircuitBreakerConfiguration.class.getName() };
        case ASPECTJ:
            return new String[] { CIRCUIT_BREAKER_ASPECT_CONFIGURATION_CLASS_NAME };
        default:
            return null;
        }
    }

}
