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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import com.developmentsprint.spring.breaker.CircuitManager;
import com.developmentsprint.spring.breaker.aspectj.AspectJCircuitBreakerConfiguration;
import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerInterceptor;

/**
 * Enables Spring's annotation-driven circuit breaker management capability, similar to the support found in Spring's {@code <breaker:*>} XML namespace. To be
 * used together with @{@link org.springframework.context.annotation.Configuration Configuration} classes as follows:
 * 
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableCircuitBreakers
 * public class AppConfig {
 * 
 *     &#064;Bean
 *     public MyService myService() {
 *         // configure and return a class having @CircuitBreaker methods
 *         return new MyService();
 *     }
 * 
 *     &#064;Bean
 *     public CircuitManager circuitManager() {
 *         // configure and return an implementation of Spring's CirctuiManager SPI
 *         return circuitManager;
 *     }
 * }
 * </pre>
 * 
 * <p>
 * For reference, the example above can be compared to the following Spring XML configuration:
 * 
 * <pre class="code">
 * {@code
 * <beans>
 *     <breaker:annotation-driven/>
 *     <bean id="myService" class="com.foo.MyService"/>
 *     <bean id="circuitManager" class="...CircuitManagerImpl">
 *       <!-- configure properties... -->
 *     </bean>
 * </beans>
 * }
 * </pre>
 * 
 * In both of the scenarios above, {@code @EnableCircuitBreakers} and {@code <breaker:annotation-driven/>} are responsible for registering the necessary Spring
 * components that power annotation-driven circuit breaker management, such as the {@link CircuitBreakerInterceptor @CircuitBreakerInterceptor} and the proxy-
 * or AspectJ-based advice that weaves the interceptor into the call stack when {@link CircuitBreaker @CircuitBreaker} methods are invoked.
 * 
 * <p>
 * <strong>A bean of type {@link CircuitManager} must be registered</strong>, as there is no reasonable default that the framework can use as a convention. And
 * whereas the {@code <breaker:annotation-driven>} element assumes a bean <em>named</em> "circuitManager", {@code EnableCircuitBreakers @EnableCircuitBreakers}
 * searches for a circuit manager bean <em>by type</em>. Therefore, naming of the circuit manager bean method is not significant.
 * 
 * <p>
 * For those that wish to establish a more direct relationship between {@code EnableCircuitBreakers @EnableCircuitBreakers} and the exact circuit manager bean
 * to be used, the {@link CircuitBreakerConfigurer} callback interface may be implemented - notice the {@code implements} clause and the {@code @Override}
 * -annotated methods below:
 * 
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableCircuitBreakers
 * public class AppConfig implements CircuitBreakerConfigurer {
 * 
 *     &#064;Bean
 *     public MyService myService() {
 *         // configure and return a class having @CircuitBreaker methods
 *         return new MyService();
 *     }
 * 
 *     &#064;Bean
 *     &#064;Override
 *     public CircuitManager circuitManager() {
 *         // configure and return an implementation of Spring's CircuitManager SPI
 *         return circuitManager;
 *     }
 * 
 * }
 * </pre>
 * 
 * This approach may be desirable simply because it is more explicit, or it may be necessary in order to distinguish between two {@code CircuitManager} beans
 * present in the same container.
 * 
 * <p>
 * The {@link #mode()} attribute controls how advice is applied; if the mode is {@link AdviceMode#PROXY} (the default), then the other attributes such as
 * {@link #proxyTargetClass()} control the behavior of the proxying.
 * 
 * <p>
 * If the {@linkplain #mode} is set to {@link AdviceMode#ASPECTJ}, then the {@link #proxyTargetClass()} attribute is obsolete. Note also that in this case the
 * {@code spring-aspects} module JAR must be present on the classpath.
 * 
 * @author Todd Orr
 * @since 1.0
 * @see CircuitBreakerConfigurer
 * @see CircuitBreakerConfigurationSelector
 * @see ProxyCircuitBreakerConfiguration
 * @see AspectJCircuitBreakerConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CircuitBreakerConfigurationSelector.class)
public @interface EnableCircuitBreakers {

    /**
     * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed to standard Java interface-based proxies. The default is {@code false}.
     * <strong> Applicable only if {@link #mode()} is set to {@link AdviceMode#PROXY}</strong>.
     * 
     * <p>
     * Note that setting this attribute to {@code true} will affect <em>all</em> Spring-managed beans requiring proxying, not just those marked with
     * {@code @CircuitBreaker}. For example, other beans marked with Spring's {@code @Transactional} annotation will be upgraded to subclass proxying at the
     * same time. This approach has no negative impact in practice unless one is explicitly expecting one type of proxy vs another, e.g. in tests.
     */
    boolean proxyTargetClass() default false;

    /**
     * Indicate how circuit breaker advice should be applied. The default is {@link AdviceMode#PROXY}.
     * 
     * @see AdviceMode
     */
    AdviceMode mode() default AdviceMode.PROXY;

    /**
     * Indicate the ordering of the execution of the circuit breaker advisor when multiple advices are applied at a specific joinpoint. The default is
     * {@link Ordered#LOWEST_PRECEDENCE}.
     */
    int order() default Ordered.LOWEST_PRECEDENCE;

}
