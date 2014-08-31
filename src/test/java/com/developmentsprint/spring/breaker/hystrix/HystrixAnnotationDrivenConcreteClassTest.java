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
package com.developmentsprint.spring.breaker.hystrix;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.developmentsprint.spring.breaker.CircuitBreakerException;
import com.developmentsprint.spring.breaker.CircuitManager;
import com.developmentsprint.spring.breaker.hystrix.test.HystrixAnnotatedFooBar;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context.xml")
@ActiveProfiles({ "annotationDriven", "hystrixManager" })
public class HystrixAnnotationDrivenConcreteClassTest {

    @Autowired
    private HystrixAnnotatedFooBar methods;

    @Autowired
    private CircuitManager circuitManager;

    @Test
    public void testFallback() {
        String name = methods.getName();
        assertThat(name).isEqualTo("Intercepted by Hystrix");
    }

    @Test
    public void testPassThroughFailure() {
        try {
            methods.throwsNullPointerException();
            fail("Shouldn't get here");
        } catch (CircuitBreakerException e) {
            assertThat(e.getCause()).isInstanceOf(NullPointerException.class);
        }
        try {
            methods.throwsIllegalArgumentException();
            fail("Shouldn't get here");
        } catch (CircuitBreakerException e) {
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void testMaxConcurrency() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        List<Future<String>> futures = new ArrayList<Future<String>>();
        for (int i = 0; i < 20; i++) {
            futures.add(executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return methods.getText();
                }
            }));
        }

        Thread.sleep(500L);

        int successes = 0;
        int rejections = 0;
        for (Future<String> future : futures) {
            try {
                future.get();
                successes++;
            } catch (Exception e) {
                rejections++;
            }
        }

        assertThat(successes).isEqualTo(10);
        assertThat(rejections).isEqualTo(10);
    }

}
