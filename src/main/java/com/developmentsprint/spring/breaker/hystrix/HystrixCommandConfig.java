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
