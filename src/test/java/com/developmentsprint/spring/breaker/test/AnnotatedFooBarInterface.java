package com.developmentsprint.spring.breaker.test;

import java.util.Date;

import com.developmentsprint.spring.breaker.annotations.CircuitBreaker;

public interface AnnotatedFooBarInterface {

    Integer getCount();

    @CircuitBreaker(commandName = "AnnotatedInterfaceGetNameMethodGuard")
    String getName();

    String getDescription();

    @CircuitBreaker(commandName = "AnnotatedInterfaceGetCreatedDateMethodGuard")
    Date getCreatedDate();

}
