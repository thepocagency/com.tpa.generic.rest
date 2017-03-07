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

import com.tpa.generic.rest.mongodbbased.generic.service.CRUDServiceAbst;
import com.tpa.generic.rest.mongodbbased.security.data.bean.AccessToken;
import com.tpa.generic.rest.mongodbbased.security.data.repository.AccessTokenRepository;
import com.tpa.generic.tools.primitivetype.exception.type.ForbiddenAccessException;
import com.tpa.generic.tools.primitivetype.exception.type.NotFoundException;
import com.tpa.generic.tools.primitivetype.exception.type.SavingException;
import com.tpa.generic.tools.primitivetype.exception.type.ServerException;
import com.tpa.generic.tools.primitivetype.exception.type.WrongInputException;
import com.tpa.generic.tools.service.global.GlobalService;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This is just a sample service. 
 * 
 * You have to define your own Access Token Service if you want a secured system !
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

@Service
public class SimpleAccessTokenService extends CRUDServiceAbst<AccessToken, AccessTokenRepository> implements GenericAccessTokenService {
    
    public static String JWT_CLAIM_ROLE_VALUE = "role";
    public static String JWT_CLAIM_FIRSTNAME_VALUE = "firstname";
    public static String JWT_CLAIM_LASTNAME_VALUE = "lastname";

    @Autowired
    public SimpleAccessTokenService(AccessTokenRepository accessTokenRepository, GlobalService globalService) {
        super(AccessToken.class, accessTokenRepository, globalService);
    }
        
    @Override
    public final AccessToken findOneByJwt(String jwt) throws NotFoundException {

        AccessToken accessToken = (AccessToken) getRepository().findOneByJwt(jwt);

        if (accessToken != null) {

            return accessToken;
        }

        throw new NotFoundException("The JWT is missing in the database");
    }
    
    @Override
    public final AccessToken checkJwt(String jwt, String applicationUuid) throws ForbiddenAccessException {
        
        try {
            AccessToken accessToken = findOneByJwt(jwt);
            
            if (!accessToken.getApplicationUuid().equals(applicationUuid)) {
                throw new NotFoundException("JWT was not created for this application");
            }
            
            return accessToken;
        }
        catch (Exception e) {
            
            throw new ForbiddenAccessException("Access forbidden for this JWT to this app", e);
        }
    }
    
    @Override
    public AccessToken login(String applicationUuid, HmacKey hmacKey, String roleValue, String userUuid, String userFirstname, String userLastname) throws ServerException, WrongInputException, SavingException {
        return save(new AccessToken(applicationUuid, userUuid, roleValue, RandomStringUtils.random(20, true, true)));
    }

    @Override
    public Boolean logout(String jwt) throws NotFoundException {

        AccessToken accessToken = findOneByJwt(jwt);

        if (accessToken != null) {

            getRepository().delete(accessToken);
            return Boolean.TRUE;
        }

        throw new NotFoundException("The JWT is missing in the DB");
    }

    @Override
    public JwtClaims createJwtClaims(String applicationUuid, String roleValue, String userUuid, String userFirstname, String userLastname) throws ServerException {
        return null;
    }

    @Override
    public JsonWebSignature getJsonWebSignature(JwtClaims claims, HmacKey hmacKey, String userUuid) throws ServerException {
        return null;
    }
    
    @Override
    public AccessToken postponeAccessToken(AccessToken accessToken) throws WrongInputException, SavingException, ServerException {
        return accessToken;
    }

    @Override
    public JwtClaims getOriginalJwtClaims(String applicationUuid, HmacKey hmacKey, String jwt) throws ForbiddenAccessException {
        return null;
    }
}
