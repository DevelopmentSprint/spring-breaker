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
