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

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.StringUtils;

import com.developmentsprint.spring.breaker.CircuitBreakerException;
import com.developmentsprint.spring.breaker.CircuitManager;

public class AopAllianceInvoker<T> implements CircuitManager.Invoker<T> {

    private final CircuitBreakerAttribute circuitBreakerAttribute;

    private final Method method;

    private final Object target;

    private final Class<?> targetClass;

    private final Object[] arguments;

    private final MethodInvocation invocation;

    public AopAllianceInvoker(MethodInvocation invocation, CircuitBreakerAttributeSource source) {
        this.invocation = invocation;

        target = invocation.getThis();

        // get backing class
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null && target != null) {
            targetClass = target.getClass();
        }
        this.targetClass = targetClass;

        method = invocation.getMethod();

        arguments = invocation.getArguments();

        circuitBreakerAttribute = source.getCircuitBreakerAttribute(method, targetClass);
    }

    public CircuitBreakerAttribute getCircuitBreakerAttribute() {
        DefaultCircuitBreakerAttribute cbAttr;
        if (circuitBreakerAttribute instanceof DefaultCircuitBreakerAttribute) {
            cbAttr = (DefaultCircuitBreakerAttribute) circuitBreakerAttribute;
        } else {
            cbAttr = new DefaultCircuitBreakerAttribute(circuitBreakerAttribute);
        }
        if (StringUtils.isEmpty(cbAttr.getName())) {
            StringBuilder args = new StringBuilder();
            if (arguments != null && arguments.length > 0) {
                for (Object arg : arguments) {
                    args.append(arg.getClass().getName()).append(", "); 
                }
                args.setLength(args.length() - 2);
            }
            String fullSignature = targetClass.getName() + "."  + method.getName() + "(" + args + ")";
            cbAttr.setName(fullSignature);
        }
        return cbAttr;
    }

    public Method getMethod() {
        return method;
    }

    public Object getTarget() {
        return target;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Object[] getArguments() {
        return arguments;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T invoke() {
        try {
            return (T) invocation.proceed();
        } catch (CircuitBreakerException e) {
            throw e;
        } catch (Throwable e) {
            throw new CircuitBreakerException(e);
        }
    }

}
