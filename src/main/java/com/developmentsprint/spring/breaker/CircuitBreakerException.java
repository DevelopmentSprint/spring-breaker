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
package com.developmentsprint.spring.breaker;

import org.springframework.core.NestedRuntimeException;

public class CircuitBreakerException extends NestedRuntimeException {

    private static final long serialVersionUID = 1L;

    public CircuitBreakerException(String msg) {
        super(msg);
    }

    public CircuitBreakerException(Throwable e) {
        super(messageFrom(e), e);
    }

    public CircuitBreakerException(String msg, Throwable cause) {
        super(msg, cause);
    }

    private static String messageFrom(Throwable e) {
        if (e != null) {
            return e.getMessage();
        } else {
            return null;
        }
    }

}
