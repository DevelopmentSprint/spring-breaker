package com.developmentsprint.spring.breaker.interceptor;

import java.lang.reflect.Method;

import lombok.Getter;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;

import com.developmentsprint.spring.breaker.CircuitManager;

@Getter
class AopAllianceInvoker implements CircuitManager.Invoker {

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

    @Override
    public Object invoke() {
        try {
            return invocation.proceed();
        } catch (Throwable ex) {
            throw new ThrowableWrapper(ex);
        }
    }

}
