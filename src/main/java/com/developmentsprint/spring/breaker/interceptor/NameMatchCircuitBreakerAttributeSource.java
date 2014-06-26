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
package com.developmentsprint.spring.breaker.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;

/**
 * Simple {@link CircuitBreakerAttributeSource} implementation that allows attributes to be matched by registered name.
 * 
 * @author Todd Orr
 * @since 1.0
 * @see #isMatch
 */
@SuppressWarnings("serial")
public class NameMatchCircuitBreakerAttributeSource implements CircuitBreakerAttributeSource, Serializable {

    /**
     * Logger available to subclasses.
     * <p>
     * Static for optimal serialization.
     */
    protected static final Logger log = LoggerFactory.getLogger(NameMatchCircuitBreakerAttributeSource.class);

    /** Keys are method names; values are {@link CircuitBreakerAttribute}s */
    private Map<String, RuleBasedCircuitBreakerAttribute> nameMap = new HashMap<String, RuleBasedCircuitBreakerAttribute>();

    /**
     * Set a name/attribute map, consisting of method names (e.g. "myMethod") and CircuitBreakerAttribute instances (or Strings to be converted to
     * CircuitBreakerAttribute instances).
     * 
     * @see CircuitBreakerAttribute
     */
    public void setNameMap(Map<String, RuleBasedCircuitBreakerAttribute> nameMap) {
        for (Map.Entry<String, RuleBasedCircuitBreakerAttribute> entry : nameMap.entrySet()) {
            addCircuitBreakerAttributeMethod(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Add an attribute for a circuit breaker method.
     * <p>
     * Method names can be exact matches, or of the pattern "xxx*", "*xxx" or "*xxx*" for matching multiple methods.
     * 
     * @param methodName
     *            the name of the method
     * @param attr
     *            attribute associated with the method
     */
    public void addCircuitBreakerAttributeMethod(String methodName, RuleBasedCircuitBreakerAttribute attr) {
        if (log.isDebugEnabled()) {
            log.debug("Adding circuit breaker method '{}' with attribute: {}", methodName, attr);
        }
        this.nameMap.put(methodName, attr);
    }

    @Override
    public CircuitBreakerAttribute getCircuitBreakerAttribute(Method method, Class<?> targetClass) {
        // look for direct name match
        String methodName = method.getName();
        CircuitBreakerAttribute attr = this.nameMap.get(methodName);

        if (attr == null) {
            // Look for most specific name match.
            String bestNameMatch = null;
            for (String mappedName : this.nameMap.keySet()) {
                if (isMatch(methodName, mappedName) &&
                        (bestNameMatch == null || bestNameMatch.length() <= mappedName.length())) {
                    attr = this.nameMap.get(mappedName);
                    bestNameMatch = mappedName;
                }
            }
        }

        return attr;
    }

    /**
     * Return if the given method name matches the mapped name.
     * <p>
     * The default implementation checks for "xxx*", "*xxx" and "*xxx*" matches, as well as direct equality. Can be overridden in subclasses.
     * 
     * @param methodName
     *            the method name of the class
     * @param mappedName
     *            the name in the descriptor
     * @return if the names match
     * @see org.springframework.util.PatternMatchUtils#simpleMatch(String, String)
     */
    protected boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NameMatchCircuitBreakerAttributeSource)) {
            return false;
        }
        NameMatchCircuitBreakerAttributeSource otherTas = (NameMatchCircuitBreakerAttributeSource) other;
        return ObjectUtils.nullSafeEquals(this.nameMap, otherTas.nameMap);
    }

    @Override
    public int hashCode() {
        return CircuitBreakerAttribute.class.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + this.nameMap;
    }

}