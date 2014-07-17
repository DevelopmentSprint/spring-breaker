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

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import com.developmentsprint.spring.breaker.interceptor.CircuitBreakerAttribute;
import com.developmentsprint.spring.breaker.interceptor.DefaultCircuitBreakerAttribute;

public class SpringCircuitBreakerAnnotationParser implements CircuitBreakerAnnotationParser, Serializable {

    private static final long serialVersionUID = 1L;

    public CircuitBreakerAttribute parseCircuitBreakerAnnotation(AnnotatedElement ae) {
        CircuitBreaker ann = AnnotationUtils.getAnnotation(ae, CircuitBreaker.class);
        if (ann != null) {
            CircuitBreakerAttribute cbAttribute = parseCircuitBreakerAnnotation(ann, ae);
            return cbAttribute;
        }
        else {
            return null;
        }
    }

    public CircuitBreakerAttribute parseCircuitBreakerAnnotation(CircuitBreaker ann, AnnotatedElement ae) {
        DefaultCircuitBreakerAttribute attr = new DefaultCircuitBreakerAttribute();
        if (StringUtils.isEmpty(ann.name()) && ae instanceof Method) {
            String name = ((Method) ae).getName();
            attr.setName(name);
        } else {
            attr.setName(ann.name());
        }
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
