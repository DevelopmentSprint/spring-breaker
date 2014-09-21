package com.developmentsprint.spring.breaker.hystrix;

import com.netflix.hystrix.HystrixCommand;

public interface CommandRunner<T, J> {

    J run(HystrixCommand<T> command);

}
