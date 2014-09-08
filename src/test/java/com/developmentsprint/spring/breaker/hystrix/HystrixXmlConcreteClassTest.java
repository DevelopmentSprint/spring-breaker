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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.developmentsprint.spring.breaker.CircuitManager;
import com.developmentsprint.spring.breaker.CircuitTimeoutException;
import com.developmentsprint.spring.breaker.hystrix.test.HystrixFooBar;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:context.xml")
@ActiveProfiles({ "hystrixXmlConfig", "hystrixManager" })
public class HystrixXmlConcreteClassTest {

    @Autowired
    private HystrixFooBar methods;

    @Autowired
    private CircuitManager circuitManager;

    @Test
    public void testFallback() {
        String name = methods.getName();
        assertThat(name).isEqualTo("Intercepted by Hystrix");
    }

    @Test(expected = CircuitTimeoutException.class)
    public void testPropertyReplacement() {
        methods.getDescription();
    }

}
