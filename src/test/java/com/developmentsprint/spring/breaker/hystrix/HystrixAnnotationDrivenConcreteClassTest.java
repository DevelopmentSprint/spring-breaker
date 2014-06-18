package com.developmentsprint.spring.breaker.hystrix;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

}
