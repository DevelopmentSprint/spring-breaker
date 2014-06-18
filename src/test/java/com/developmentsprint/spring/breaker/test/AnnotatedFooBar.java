package com.developmentsprint.spring.breaker.test;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.developmentsprint.spring.breaker.annotations.CircuitBreaker;

public class AnnotatedFooBar {

    private static final Logger log = LoggerFactory.getLogger(AnnotatedFooBar.class);

    public Integer getCount() {
        log.info("Returning count");
        return 10;
    }

    @CircuitBreaker(commandName = "AnnotatedGetNameMethodGuard")
    public String getName() {
        log.info("Returning name");
        return "FooBar";
    }

    public String getDescription() {
        log.info("Returning description");
        return "A mighty FooBar!";
    }

    @CircuitBreaker(commandName = "AnnotatedGetCreatedDateMethodGuard")
    public Date getCreatedDate() {
        log.info("Returning created date");
        return new Date();
    }

}
