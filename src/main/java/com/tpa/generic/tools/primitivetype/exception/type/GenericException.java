/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tpa.generic.tools.primitivetype.exception.type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

public abstract class GenericException extends Exception {

    protected static Logger logger = LogManager.getLogger();
    protected final HttpStatus httpStatus;

    public GenericException(String level, HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        
        logException(level);
    }

    public GenericException(String level, HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        
        logException(level);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    private void logException(String level) {
        
        try {
            Method method = logger.getClass().getMethod(level, String.class);
            method.invoke(logger, getMessage());
            
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.error("Could not generate the dynamic log:");
            logger.error(getMessage());
        }   
    }
}
