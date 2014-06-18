package com.developmentsprint.spring.breaker.hystrix.fallback;

public interface HystrixFallback<T> {

    public T fallback();

}
