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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerInterceptor;
import com.developmentsprint.spring.breaker.interceptor.DefaultCircuitBreakerAttribute;
import com.developmentsprint.spring.breaker.interceptor.NameMatchCircuitBreakerAttributeSource;
import com.developmentsprint.spring.breaker.interceptor.RuleBasedCircuitBreakerAttribute;

public class BreakerAdviceParser extends AbstractSingleBeanDefinitionParser {

    private static Logger log = LoggerFactory.getLogger(BreakerAdviceParser.class);

    private static final String CB_MANAGER_NAME = "circuitManager";

    private static final String CB_ELEMENT_NAME = "circuit-breaker";

    private static final String CB_NAME_ATTRIBUTE = "name";

    private static final String METHOD_NAME_ATTRIBUTE = "method";

    @Override
    protected Class<?> getBeanClass(Element element) {
        return CircuitBreakerInterceptor.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addPropertyReference(CB_MANAGER_NAME, BreakerNamespaceHandler.extractCircuitManager(element));

        List<Element> cbAttributes = DomUtils.getChildElementsByTagName(element, CB_ELEMENT_NAME);
        if (cbAttributes.size() > 0) {
            // Using attributes source.
            RootBeanDefinition attributeSourceDefinition = parseAttributeSource(cbAttributes, parserContext);
            builder.addPropertyValue("circuitBreakerAttributeSource", attributeSourceDefinition);

        } else {
            // Assume annotations source.
            builder.addPropertyValue("circuitBreakerAttributeSource",
                    new RootBeanDefinition("com.developmentsprint.spring.breaker.annotations.AnnotationCircuitBreakerAttributeSource"));
        }
    }

    private RootBeanDefinition parseAttributeSource(List<Element> methods, ParserContext parserContext) {
        ManagedMap<TypedStringValue, DefaultCircuitBreakerAttribute> circuitBreakerAttributeMap =
                new ManagedMap<TypedStringValue, DefaultCircuitBreakerAttribute>(methods.size());

        circuitBreakerAttributeMap.setSource(parserContext.extractSource(methods));

        for (Element methodEle : methods) {
            String methodName = methodEle.getAttribute(METHOD_NAME_ATTRIBUTE);
            TypedStringValue nameHolder = new TypedStringValue(methodName);
            nameHolder.setSource(parserContext.extractSource(methodEle));

            RuleBasedCircuitBreakerAttribute attribute = new RuleBasedCircuitBreakerAttribute();
            attribute.setMethodName(methodName);

            String cbName = methodEle.getAttribute(CB_NAME_ATTRIBUTE);
            if (StringUtils.isEmpty(cbName)) {
                attribute.setName(methodName);
            } else {
                attribute.setName(cbName);
            }

            ManagedMap<String, String> props = new ManagedMap<String, String>();
            Element propsElement = DomUtils.getChildElementByTagName(methodEle, "properties");
            if (propsElement != null) {
                //Map<String, String> props = new HashMap<String, String>();
                List<Element> propElements = DomUtils.getChildElementsByTagName(propsElement, "prop");
                for (Element propElement : propElements) {
                    String key = propElement.getAttribute("key");
                    String val = DomUtils.getTextValue(propElement);
                    props.put(key, new TypedStringValue(val).getValue());
                }
                attribute.setProperties(props);
            }
            if (log.isDebugEnabled()) {
                for (Map.Entry<String, String> e : attribute.getProperties().entrySet()) {
                    log.debug("{} prop : {} : {}", cbName, e.getKey(), e.getValue());
                }
            }

            circuitBreakerAttributeMap.put(nameHolder, attribute);
        }

        RootBeanDefinition attributeSourceDefinition = new RootBeanDefinition(NameMatchCircuitBreakerAttributeSource.class);
        attributeSourceDefinition.setSource(parserContext.extractSource(methods));
        attributeSourceDefinition.getPropertyValues().add("nameMap", circuitBreakerAttributeMap);
        return attributeSourceDefinition;
    }

}
