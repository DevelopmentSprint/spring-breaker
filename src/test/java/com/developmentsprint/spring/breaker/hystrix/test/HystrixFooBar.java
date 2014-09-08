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

public class HystrixFooBar {

    private static final Logger log = LoggerFactory.getLogger(HystrixFooBar.class);

    public Integer getCount() {
        log.info("Returning count");
        return 10;
    }

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
        try {
            Thread.sleep(750L);
        } catch (InterruptedException e) {
        }
        return "A mighty FooBar!";
    }

    public Date getCreatedDate() {
        log.info("Returning created date");
        return new Date();
    }

}
