package com.developmentsprint.spring.breaker.hystrix.test;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.developmentsprint.spring.breaker.annotations.CircuitBreaker;
import com.developmentsprint.spring.breaker.annotations.CircuitProperty;

public class HystrixAnnotatedFooBar {

    private static final Logger log = LoggerFactory.getLogger(HystrixAnnotatedFooBar.class);

    public Integer getCount() {
        log.info("Returning count");
        return 10;
    }

    @CircuitBreaker(commandName = "AnnotatedGetNameMethodGuard", properties = {
            @CircuitProperty(key = "fallbackClass", value = "com.developmentsprint.spring.breaker.hystrix.test.HystrixFallback")
    })
    public String getName() {
        log.info("Returning name");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
        }
        return "FooBar";
    }

    public String getDescription() {
        log.info("Returning description");
        return "A mighty FooBar!";
    }

    public Date getCreatedDate() {
        log.info("Returning created date");
        return new Date();
    }

}
