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
package com.tpa.generic.rest.mongodbbased.security.service;

import com.tpa.generic.rest.mongodbbased.security.data.bean.AccessToken;
import com.tpa.generic.tools.primitivetype.exception.type.ForbiddenAccessException;
import com.tpa.generic.tools.primitivetype.exception.type.NotFoundException;
import com.tpa.generic.tools.primitivetype.exception.type.SavingException;
import com.tpa.generic.tools.primitivetype.exception.type.ServerException;
import com.tpa.generic.tools.primitivetype.exception.type.WrongInputException;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */
public interface GenericAccessTokenService {

    AccessToken findOneByJwt(String jwt) throws NotFoundException;
    
    AccessToken checkJwt(String jwt, String applicationUuid) throws ForbiddenAccessException;
    
    AccessToken login(String applicationUuid, HmacKey hmacKey, String roleValue, String userUuid, String userFirstname, String userLastname) throws ServerException, WrongInputException, SavingException;
    
    Boolean logout(String jwt) throws NotFoundException;
    
    JwtClaims createJwtClaims(String applicationUuid, String roleValue, String userUuid, String userFirstname, String userLastname) throws ServerException;
    
    JsonWebSignature getJsonWebSignature(JwtClaims claims, HmacKey hmacKey, String userUuid) throws ServerException;
    
    AccessToken postponeAccessToken(AccessToken accessToken) throws WrongInputException, SavingException, ServerException;
    
    JwtClaims getOriginalJwtClaims(String applicationUuid, HmacKey hmacKey, String jwt) throws ForbiddenAccessException;
}
