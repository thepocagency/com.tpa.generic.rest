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

package com.tpa.generic.tools.service.exception;

import com.tpa.generic.tools.primitivetype.exception.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.tpa.generic.tools.primitivetype.exception.type.GenericException;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

@Service
public class ErrorService {
    
    public ResponseEntity<Object> getErrorResponse(GenericException genericException) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setStatus(genericException.getHttpStatus().value());
        errorMessage.setError(genericException.getHttpStatus().getReasonPhrase());
        errorMessage.setException(genericException.getClass().toGenericString());
        
        errorMessage.setMessage(genericException.getMessage());
        errorMessage.setShowUserMessage(true);
        errorMessage.setPath(null);
        
        return new ResponseEntity<>(errorMessage, genericException.getHttpStatus());
    }
    
    public ResponseEntity<Object> getErrorResponse(GenericException genericException, Boolean showUserMessage) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setStatus(genericException.getHttpStatus().value());
        errorMessage.setError(genericException.getHttpStatus().getReasonPhrase());
        errorMessage.setException(genericException.getClass().toGenericString());
        
        errorMessage.setMessage(genericException.getMessage());
        errorMessage.setShowUserMessage(showUserMessage);
        errorMessage.setPath(null);
        
        return new ResponseEntity<>(errorMessage, genericException.getHttpStatus());
    }
}
