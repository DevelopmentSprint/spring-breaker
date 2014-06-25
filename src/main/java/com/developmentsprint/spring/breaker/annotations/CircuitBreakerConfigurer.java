package com.developmentsprint.spring.breaker.annotations;

import com.developmentsprint.spring.breaker.CircuitManager;

/**
 * Interface to be implemented by @{@link org.springframework.context.annotation.Configuration Configuration} classes annotated with @
 * {@link EnableCircuitBreakers} that wish or need to specify explicitly the {@link CircuitManager} bean to be used for annotation-driven circuit breaker
 * management.
 * 
 * <p>
 * See @{@link EnableCircuitBreakers} for general examples and context; see {@link #circuitManager()} for detailed instructions.
 * 
 * @author Todd Orr
 * @since 1.0
 * @see EnableCircuitBreakers
 */
public interface CircuitBreakerConfigurer {

    /**
     * Return the circuit manager bean to use for annotation-driven circuit breaker management. Implementations must explicitly declare
     * {@link org.springframework.context.annotation.Bean @Bean}, e.g.
     * 
     * <pre class="code">
     * &#064;Configuration
     * &#064;EnableCircuitBreakers
     * public class AppConfig implements CircuitBreakerConfigurer {
     *     &#064;Bean
     *     // important!
     *     &#064;Override
     *     public CircuitManager circuitManager() {
     *         // configure and return CircuitManager instance
     *     }
     *     // ...
     * }
     * </pre>
     * 
     * See @{@link EnableCircuitBreakers} for more complete examples.
     */
    CircuitManager circuitManager();

}