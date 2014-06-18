package com.developmentsprint.spring.breaker.hystrix.fallback;


public final class FailSilentFallback<T> implements HystrixFallback<T> {

    @Override
    public T fallback() {
        // do nothing
        return null;
    }

}
