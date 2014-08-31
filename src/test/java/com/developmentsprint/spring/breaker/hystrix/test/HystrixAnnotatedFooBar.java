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

    @CircuitBreaker(name = "AnnotatedGetNameMethodGuard", properties = {
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

    @CircuitBreaker(name = "AnnotatedGetTextMethodGuard", properties = {
            @CircuitProperty(key = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    })
    public String getText() {
        log.info("Returning text");
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            // do nothing
        }
        return "A pathetic FooBar...";
    }

    @CircuitBreaker(name = "AnnotatedThrowsNullPointerExceptionMethodGuard")
    public String throwsNullPointerException() {
        throw new NullPointerException("This was explicitly thrown");
    }

    @CircuitBreaker(name = "AnnotatedThrowsIllegalArgumentExceptionMethodGuard")
    public void throwsIllegalArgumentException() {
        throw new IllegalArgumentException("Bad argument");
    }

}
