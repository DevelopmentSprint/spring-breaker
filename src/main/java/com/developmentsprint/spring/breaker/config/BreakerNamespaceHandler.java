package com.developmentsprint.spring.breaker.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.w3c.dom.Element;

/**
 * {@code NamespaceHandler} allowing for the configuration of declarative circuit breaker management using either XML or using annotations.
 * <p>
 * This namespace handler is the central piece of functionality in the Spring circuit breaker management facilities.
 * 
 * @author Todd Orr
 * @since 1.0
 */
public final class BreakerNamespaceHandler extends NamespaceHandlerSupport {

    static final String CIRCUIT_MANAGER_ATTRIBUTE = "circuit-manager";

    static final String DEFAULT_CIRCUIT_MANAGER_BEAN_NAME = "circuitManager";

    @Override
    public void init() {
        registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBreakerBeanDefinitionParser());
        registerBeanDefinitionParser("advice", new BreakerAdviceParser());
    }

    public static String extractCircuitManager(Element element) {
        return (element.hasAttribute(BreakerNamespaceHandler.CIRCUIT_MANAGER_ATTRIBUTE) ? element
                .getAttribute(BreakerNamespaceHandler.CIRCUIT_MANAGER_ATTRIBUTE)
                : BreakerNamespaceHandler.DEFAULT_CIRCUIT_MANAGER_BEAN_NAME);
    }

}
