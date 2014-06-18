package com.developmentsprint.spring.breaker.hystrix;

import lombok.Getter;
import lombok.experimental.Builder;

@Getter
@Builder
public class HystrixCommandConfig {

    /**
     * https://github.com/Netflix/Hystrix/wiki/How-To-Use#CommandName
     */
    private final String key;

    /**
     * https://github.com/Netflix/Hystrix/wiki/How-To-Use#CommandGroup
     */
    private final String groupKey;

    /**
     * https://github.com/Netflix/Hystrix/wiki/How-To-Use#command-thread-pool
     */
    private final String threadPoolKey;

    /**
     * https://github.com/Netflix/Hystrix/wiki/How-To-Use#request-cache
     */
    private final String cacheKey;

    /**
     * https://github.com/Netflix/Hystrix/wiki/Configuration#executionisolationstrategy
     */
    private final String executionIsolationStrategy;

    /**
     * https://github.com/Netflix/Hystrix/wiki/Configuration#executionisolationthreadtimeoutinmilliseconds
     */
    private final String executionIsolationThreadTimeout;

    /**
     * https://github.com/Netflix/Hystrix/wiki/Configuration#fallback
     */
    private final String fallbackClass;

    /**
     * https://github.com/Netflix/Hystrix/wiki/Configuration#fallbackisolationsemaphoremaxconcurrentrequests
     */
    private final Integer fallbackIsolationSemaphoreMaxConcurrentRequests;

}
