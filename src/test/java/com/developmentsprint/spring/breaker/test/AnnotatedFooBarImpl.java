package com.developmentsprint.spring.breaker.test;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotatedFooBarImpl implements AnnotatedFooBarInterface {

    private static final Logger log = LoggerFactory.getLogger(AnnotatedFooBarImpl.class);

    public Integer getCount() {
        log.info("Returning count");
        return 10;
    }

    public String getName() {
        log.info("Returning name");
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
