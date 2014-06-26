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

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.developmentsprint.spring.breaker.annotations.AnnotationCircuitBreakerAttributeSource;
import com.developmentsprint.spring.breaker.annotations.ProxyCircuitBreakerConfiguration;
import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttributeSourceAdvisor;
import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerInterceptor;

/**
 * {@link org.springframework.beans.factory.xml.BeanDefinitionParser} implementation that allows users to easily configure all the infrastructure beans required
 * to enable annotation-driven circuti breaker demarcation.
 * 
 * <p>
 * By default, all proxies are created as JDK proxies. This may cause some problems if you are injecting objects as concrete classes rather than interfaces. To
 * overcome this restriction you can set the '{@code proxy-target-class}' attribute to '{@code true}', which will result in class-based proxies being created.
 * 
 * @author Todd Orr
 * @since 1.0
 */
final class AnnotationDrivenBreakerBeanDefinitionParser implements BeanDefinitionParser {

    /**
     * Parses the '{@code <breaker:annotation-driven>}' tag. Will {@link AopNamespaceUtils#registerAutoProxyCreatorIfNecessary register an AutoProxyCreator}
     * with the container as necessary.
     */
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String mode = element.getAttribute("mode");
        if ("aspectj".equals(mode)) {
            // mode="aspectj"
            // registerCircuitBreaderAspect(element, parserContext);
        } else {
            // mode="proxy"
            AopAutoProxyConfigurer.configureAutoProxyCreator(element, parserContext);
        }
        return null;
    }

    private static void parseCircuitManagerProperty(Element element, BeanDefinition def) {
        def.getPropertyValues().add("circuitManager",
                new RuntimeBeanReference(BreakerNamespaceHandler.extractCircuitManager(element)));
    }

    /**
     * Inner class to just introduce an AOP framework dependency when actually in proxy mode.
     */
    private static class AopAutoProxyConfigurer {

        public static void configureAutoProxyCreator(Element element, ParserContext parserContext) {
            AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext, element);

            if (!parserContext.getRegistry().containsBeanDefinition(ProxyCircuitBreakerConfiguration.CIRCUIT_BREAKER_ADVISOR_BEAN_NAME)) {
                Object eleSource = parserContext.extractSource(element);

                // Create the AnnotationCircuitBreakerAttributeSource definition.
                RootBeanDefinition sourceDef = new RootBeanDefinition(AnnotationCircuitBreakerAttributeSource.class);
                sourceDef.setSource(eleSource);
                sourceDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                String sourceName = parserContext.getReaderContext().registerWithGeneratedName(sourceDef);

                // Create the CircuitBreakerInterceptor definition.
                RootBeanDefinition interceptorDef = new RootBeanDefinition(CircuitBreakerInterceptor.class);
                interceptorDef.setSource(eleSource);
                interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                parseCircuitManagerProperty(element, interceptorDef);
                interceptorDef.getPropertyValues().add("circuitBreakerAttributeSource", new RuntimeBeanReference(sourceName));
                String interceptorName = parserContext.getReaderContext().registerWithGeneratedName(interceptorDef);

                // Create the CircuitBreakerAttributeSourceAdvisor definition.
                RootBeanDefinition advisorDef = new RootBeanDefinition(CircuitBreakerAttributeSourceAdvisor.class);
                advisorDef.setSource(eleSource);
                advisorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
                advisorDef.getPropertyValues().add("circuitBreakerInterceptor", new RuntimeBeanReference(interceptorName));
                if (element.hasAttribute("order")) {
                    advisorDef.getPropertyValues().add("order", element.getAttribute("order"));
                }
                parserContext.getRegistry().registerBeanDefinition(ProxyCircuitBreakerConfiguration.CIRCUIT_BREAKER_ADVISOR_BEAN_NAME, advisorDef);

                CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(),
                        eleSource);
                compositeDef.addNestedComponent(new BeanComponentDefinition(sourceDef, sourceName));
                compositeDef.addNestedComponent(new BeanComponentDefinition(interceptorDef, interceptorName));
                compositeDef.addNestedComponent(new BeanComponentDefinition(advisorDef, ProxyCircuitBreakerConfiguration.CIRCUIT_BREAKER_ADVISOR_BEAN_NAME));
                parserContext.registerComponent(compositeDef);
            }
        }
    }
}
