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
