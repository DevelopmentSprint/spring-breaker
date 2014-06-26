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


public class HystrixCommandConfig {

    /**
     * https://github.com/Netflix/Hystrix/wiki/How-To-Use#CommandName
     */
    private String key;

    /**
     * https://github.com/Netflix/Hystrix/wiki/How-To-Use#CommandGroup
     */
    private String groupKey;

    /**
     * https://github.com/Netflix/Hystrix/wiki/How-To-Use#command-thread-pool
     */
    private String threadPoolKey;

    /**
     * https://github.com/Netflix/Hystrix/wiki/How-To-Use#request-cache
     */
    private String cacheKey;

    /**
     * https://github.com/Netflix/Hystrix/wiki/Configuration#executionisolationstrategy
     */
    private String executionIsolationStrategy;

    /**
     * https://github.com/Netflix/Hystrix/wiki/Configuration#executionisolationthreadtimeoutinmilliseconds
     */
    private String executionIsolationThreadTimeout;

    /**
     * https://github.com/Netflix/Hystrix/wiki/Configuration#fallback
     */
    private String fallbackClass;

    /**
     * https://github.com/Netflix/Hystrix/wiki/Configuration#fallbackisolationsemaphoremaxconcurrentrequests
     */
    private Integer fallbackIsolationSemaphoreMaxConcurrentRequests;

}
