package com.developmentsprint.spring.breaker.annotations;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.annotation.AnnotationUtils;

import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttribute;
import com.developmentsprint.spring.breaker.interceptor.DefaultCircuitBreakerAttribute;

public class SpringCircuitBreakerAnnotationParser implements CircuitBreakerAnnotationParser, Serializable {

    private static final long serialVersionUID = 1L;

    public CircuitBreakerAttribute parseCircuitBreakerAnnotation(AnnotatedElement ae) {
        CircuitBreaker ann = AnnotationUtils.getAnnotation(ae, CircuitBreaker.class);
        if (ann != null) {
            return parseCircuitBreakerAnnotation(ann);
        }
        else {
            return null;
        }
    }

    public CircuitBreakerAttribute parseCircuitBreakerAnnotation(CircuitBreaker ann) {
        DefaultCircuitBreakerAttribute attr = new DefaultCircuitBreakerAttribute();
        attr.setName(ann.commandName());
        if (ann.properties().length > 0) {
            Map<String, String> properties = new HashMap<String, String>();
            for (CircuitProperty prop : ann.properties()) {
                properties.put(prop.key(), prop.value());
            }
            attr.setProperties(properties);
        }
        return attr;
    }

    @Override
    public boolean equals(Object other) {
        return (this == other || other instanceof SpringCircuitBreakerAnnotationParser);
    }

    @Override
    public int hashCode() {
        return SpringCircuitBreakerAnnotationParser.class.hashCode();
    }

}
