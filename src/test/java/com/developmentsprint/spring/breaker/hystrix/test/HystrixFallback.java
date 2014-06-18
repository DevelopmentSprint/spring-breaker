package com.developmentsprint.spring.breaker.hystrix.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HystrixFallback implements com.developmentsprint.spring.breaker.hystrix.fallback.HystrixFallback<Object> {

    private static final Logger log = LoggerFactory.getLogger(HystrixFallback.class);

    @Override
    public Object fallback() {
        log.info("Ran hystrix fallback");
        return "Intercepted by Hystrix";
    }

}
