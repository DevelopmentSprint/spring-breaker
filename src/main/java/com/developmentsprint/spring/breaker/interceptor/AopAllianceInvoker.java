package com.developmentsprint.spring.breaker.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.StringUtils;

import com.developmentsprint.spring.breaker.CircuitManager;

public class AopAllianceInvoker implements CircuitManager.Invoker {

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

    @Override
    public Object invoke() {
        try {
            return invocation.proceed();
        } catch (Throwable ex) {
            throw new ThrowableWrapper(ex);
        }
    }

}
