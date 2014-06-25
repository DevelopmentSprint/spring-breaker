package com.developmentsprint.spring.breaker.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerInterceptor;
import com.developmentsprint.spring.breaker.interceptor.DefaultCircuitBreakerAttribute;
import com.developmentsprint.spring.breaker.interceptor.NameMatchCircuitBreakerAttributeSource;
import com.developmentsprint.spring.breaker.interceptor.RuleBasedCircuitBreakerAttribute;

public class BreakerAdviceParser extends AbstractSingleBeanDefinitionParser {

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
        }
        else {
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
            attribute.setName(cbName);

            Element propsElement = DomUtils.getChildElementByTagName(methodEle, "properties");
            if (propsElement != null) {
                Map<String, String> props = new HashMap<String, String>();
                List<Element> propElements = DomUtils.getChildElementsByTagName(propsElement, "prop");
                for (Element propElement : propElements) {
                    String key = propElement.getAttribute("key");
                    String val = DomUtils.getTextValue(propElement);
                    props.put(key, val);
                }
                attribute.setProperties(props);
            }

            circuitBreakerAttributeMap.put(nameHolder, attribute);
        }

        RootBeanDefinition attributeSourceDefinition = new RootBeanDefinition(NameMatchCircuitBreakerAttributeSource.class);
        attributeSourceDefinition.setSource(parserContext.extractSource(methods));
        attributeSourceDefinition.getPropertyValues().add("nameMap", circuitBreakerAttributeMap);
        return attributeSourceDefinition;
    }

}
