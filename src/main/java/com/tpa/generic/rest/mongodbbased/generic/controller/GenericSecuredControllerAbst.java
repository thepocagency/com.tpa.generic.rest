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

package com.tpa.generic.rest.mongodbbased.generic.controller;

import com.tpa.generic.rest.mongodbbased.generic.service.CRUDServiceAbst;
import com.tpa.generic.rest.mongodbbased.security.data.bean.AccessToken;
import com.tpa.generic.rest.mongodbbased.security.data.bean.Role;
import com.tpa.generic.rest.mongodbbased.security.service.GenericAccessTokenService;
import com.tpa.generic.tools.primitivetype.exception.type.ForbiddenAccessException;
import com.tpa.generic.tools.primitivetype.exception.type.GenericException;
import com.tpa.generic.tools.primitivetype.exception.type.NotImplementedException;
import com.tpa.generic.tools.primitivetype.exception.type.SavingException;
import com.tpa.generic.tools.primitivetype.exception.type.ServerException;
import com.tpa.generic.tools.primitivetype.exception.type.WrongInputException;
import com.tpa.generic.tools.primitivetype.string.StringUtils;
import com.tpa.generic.tools.primitivetype.uri.RequestUtils;
import com.tpa.generic.tools.service.global.GlobalService;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 * @param <ACCESS_TOKEN_SERVICE>
 * @param <INPUT_ENTITY_TYPE>
 * @param <SERVICE_TYPE> 
 */

public abstract class GenericSecuredControllerAbst<ACCESS_TOKEN_SERVICE extends GenericAccessTokenService, INPUT_ENTITY_TYPE extends Object, SERVICE_TYPE extends CRUDServiceAbst> {

    protected static Logger logger = LogManager.getLogger();
    protected static String TEXT_METHOD_NOT_IMPLEMENTED = "Method not implemented";
    
    private final GlobalService globalService;
    
    private final ACCESS_TOKEN_SERVICE accessTokenService;
    
    private final SERVICE_TYPE mainService;
    
    private Map<String, List<Role>> rights;
    
    public GenericSecuredControllerAbst(GlobalService globalService, ACCESS_TOKEN_SERVICE accessTokenService, SERVICE_TYPE mainService) {
        this.globalService = globalService;
        this.accessTokenService = accessTokenService;
        this.mainService = mainService;
    }
    
    public GenericSecuredControllerAbst(GlobalService globalService, ACCESS_TOKEN_SERVICE accessTokenService, SERVICE_TYPE mainService, Map<String, List<Role>> rights) throws ForbiddenAccessException {
        this.globalService = globalService;
        this.accessTokenService = accessTokenService;
        this.mainService = mainService;
        
        setRights(rights);
    }
    
    //
    
    private Boolean isVisitorAccess(Map<String, List<Role>> rights, String method) throws ForbiddenAccessException {
        
        if (rights == null
                || rights.isEmpty()
                || method == null
                || !RequestUtils.getAllMethods().contains(method)) {
            throw new ForbiddenAccessException("Rights not defined for the method " + StringUtils.getNullStringIfNullObject(method));
        }
        
        if (rights.get(method).contains(Role.FORBIDDEN)) {
            throw new ForbiddenAccessException("Method " + StringUtils.getNullStringIfNullObject(method) + " is forbidden");
        }
        
        if (rights.get(method).contains(Role.VISITOR)) {
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }
       
    private AccessToken checkRigths(Map<String, List<Role>> rights, String method, String jwt, String applicationUuid,
            Boolean isJwtHeaderOptional, Boolean isAppUuidHeaderOptional) throws ForbiddenAccessException {
        
        if (isVisitorAccess(rights, method)
                && (StringUtils.isUnvalidString(jwt)
                    && isJwtHeaderOptional)
                && (StringUtils.isUnvalidString(applicationUuid)
                    && isAppUuidHeaderOptional)) {
            return null;
        }
        
        if (((StringUtils.isUnvalidString(jwt)
                && !isJwtHeaderOptional)
                || (StringUtils.isUnvalidString(applicationUuid)
                    && !isAppUuidHeaderOptional))) {
            throw new ForbiddenAccessException("Application UUID or JWT can not be null or empty");
        }
        
        AccessToken accessToken = getAccessTokenService().checkJwt(jwt, applicationUuid);
        List<Role> allowedRoles = rights.get(method);
        
        switch (Role.valueOf(accessToken.getRoleValue())) {
            case THE_DUDE:
                return accessToken;
                
            case COMPANY_ADMIN:
                if ((allowedRoles.contains(Role.VISITOR)
                        || allowedRoles.contains(Role.APP_USER)
                        || allowedRoles.contains(Role.APP_ADMIN)
                        || allowedRoles.contains(Role.GROUP_ADMIN)
                        || allowedRoles.contains(Role.COMPANY_ADMIN))
                        && accessToken.getRoleValue().equals(Role.COMPANY_ADMIN.getValue())) {
                    return accessToken;
                }
                
            case GROUP_ADMIN:
                if ((allowedRoles.contains(Role.VISITOR)
                        || allowedRoles.contains(Role.APP_USER)
                        || allowedRoles.contains(Role.APP_ADMIN)
                        || allowedRoles.contains(Role.GROUP_ADMIN))
                        && accessToken.getRoleValue().equals(Role.GROUP_ADMIN.getValue())) {
                    return accessToken;
                }
                
            case APP_ADMIN:
                if (allowedRoles.contains(Role.VISITOR)
                        || allowedRoles.contains(Role.APP_USER)
                        || allowedRoles.contains(Role.APP_ADMIN)
                        && accessToken.getRoleValue().equals(Role.APP_ADMIN.getValue())) {
                    return accessToken;
                }
                
            case APP_USER:
                if (allowedRoles.contains(Role.VISITOR)
                        || allowedRoles.contains(Role.APP_USER)
                        && accessToken.getRoleValue().equals(Role.APP_USER.getValue())) {
                    return accessToken;
                }
                
            case VISITOR:
                if (!allowedRoles.contains(Role.FORBIDDEN)
                        && allowedRoles.contains(Role.VISITOR)) {
                    return accessToken;
                }
                
            default:
                throw new ForbiddenAccessException("User is not allowed");
        }
    }
    
    //
    
    protected GlobalService getGlobalService() {
        return globalService;
    }

    protected ACCESS_TOKEN_SERVICE getAccessTokenService() {
        return accessTokenService;
    }

    protected SERVICE_TYPE getMainService() {
        return (SERVICE_TYPE) mainService;
    }
    
    protected Map<String, List<Role>> getRights() throws ForbiddenAccessException {
        return rights;
    }

    protected final void setRights(Map<String, List<Role>> rights) throws ForbiddenAccessException {
        RequestUtils.checkGlobalControllerRigths(rights);
        this.rights = rights;
    }
    
    protected ResponseEntity<Object> getErrorResponse(GenericException genericException) {
        return getGlobalService().getErrorService().getErrorResponse(genericException);
    }
    
    protected ResponseEntity<Object> getErrorResponse(GenericException genericException, Boolean showUserMessage) {
        return getGlobalService().getErrorService().getErrorResponse(genericException, showUserMessage);
    }
    
    protected String getSearch(HttpServletRequest request) {
        return request.getParameter("search");
    }
    
    protected Pageable getPagination(HttpServletRequest request) {
        try {
            int page = Integer.parseInt(request.getParameter("pageIndex"));
            int pageSize = Integer.parseInt(request.getParameter("pageSize"));

            return new PageRequest(page, pageSize);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    protected AccessToken checkRigths(String method, HttpHeaders httpHeaders) throws ForbiddenAccessException {
        
        try {
            Boolean isJwtHeaderOptional = globalService.getAppConfig().getBoolValue("app.security.httpHeaders.jwtHeaderName.isOptionnal");
            Boolean isAppUuidHeaderOptional = globalService.getAppConfig().getBoolValue("app.security.httpHeaders.appUuidHeaderName.isOptionnal");
            
            AccessToken accessToken = checkRigths(rights, method,
                    RequestUtils.getHeaderValue(httpHeaders, globalService.getAppConfig().getStringValue("generic.security.httpHeaders.jwtHeaderName"), 
                            isJwtHeaderOptional),
                    RequestUtils.getHeaderValue(httpHeaders, globalService.getAppConfig().getStringValue("generic.security.httpHeaders.appUuidHeaderName"), 
                            isAppUuidHeaderOptional), 
                    isJwtHeaderOptional, isAppUuidHeaderOptional);
            
            return getAccessTokenService().postponeAccessToken(accessToken);
        } catch (SavingException | WrongInputException | ServerException e) {
            
            throw new ForbiddenAccessException("User is not allowed", e);
        }
    }
    
    protected AccessToken checkRigths(String method, Cookie[] cookies) throws ForbiddenAccessException {
        
        try {
            Boolean isJwtHeaderOptional = globalService.getAppConfig().getBoolValue("app.security.httpHeaders.jwtHeaderName.isOptionnal");
            Boolean isAppUuidHeaderOptional = globalService.getAppConfig().getBoolValue("app.security.httpHeaders.appUuidHeaderName.isOptionnal");
            
            AccessToken accessToken = checkRigths(rights, method, 
                    RequestUtils.getCookieValue(cookies, globalService.getAppConfig().getStringValue("generic.security.httpHeaders.jwtHeaderName"), 
                            isJwtHeaderOptional),
                    RequestUtils.getCookieValue(cookies, globalService.getAppConfig().getStringValue("generic.security.httpHeaders.appUuidHeaderName"), 
                            isAppUuidHeaderOptional),
                    isJwtHeaderOptional, isAppUuidHeaderOptional);
            
            return getAccessTokenService().postponeAccessToken(accessToken);
        } catch (SavingException | WrongInputException | ServerException e) {
            
            throw new ForbiddenAccessException("User is not allowed", e);
        }
    }
    
    protected AccessToken checkRigthsForSpecificUserOnly(String method, HttpHeaders httpHeaders, String userUuid) throws ForbiddenAccessException {
        
        try {
            AccessToken accessToken = checkRigths(method, httpHeaders);
            
            if (!rights.get(method).contains(Role.VISITOR) && !rights.get(method).contains(Role.APP_USER)) {
                if (StringUtils.isUnvalidString(userUuid)
                        || !accessToken.getUserUuid().equals(userUuid)) {
                    throw new ForbiddenAccessException("You can only access your profile data");
                }
            }
            
            return getAccessTokenService().postponeAccessToken(accessToken);
        } catch (SavingException | WrongInputException | ServerException e) {
            
            throw new ForbiddenAccessException("User is not allowed", e);
        }
    }
    
    protected HttpServletResponse resetCookies(HttpServletRequest request, HttpServletResponse response) {
        if (request != null
                && response != null
                && request.getCookies() != null
                && request.getCookies().length > 0) {
            
            Cookie[] cookies = request.getCookies();
            
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                
                cookie.setValue(null);
                cookie.setMaxAge(0);
                
                response.addCookie(cookie);
            }
        }
        
        return response;
    }
    
    protected HttpServletResponse addCookie(HttpServletResponse response, String key, String value) throws ServerException {
        if (response != null
                && key != null
                && !key.isEmpty()
                && value != null
                && !value.isEmpty()) {
            
            Cookie cookie = new Cookie(key, value);
            cookie.setMaxAge(globalService.getAppConfig().getIntValue("generic.security.defaultExpirationAccessTokenTimeMinutes")*60);
            
            cookie.setDomain(globalService.getAppConfig().getStringValue("app.domain")); 
            cookie.setPath("/");
            
            response.addCookie(cookie);
        }
        
        return response;
    }
    
    //
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAll(@RequestHeader HttpHeaders httpHeaders, HttpServletRequest request, HttpServletResponse response) {
        
        return getErrorResponse(new NotImplementedException(TEXT_METHOD_NOT_IMPLEMENTED, null));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity get(@PathVariable("id") String id, 
            @RequestHeader HttpHeaders httpHeaders, HttpServletRequest request, HttpServletResponse response) {
        
        return getErrorResponse(new NotImplementedException(TEXT_METHOD_NOT_IMPLEMENTED, null));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity save(@Valid @RequestBody INPUT_ENTITY_TYPE entity, 
            @RequestHeader HttpHeaders httpHeaders, HttpServletRequest request, HttpServletResponse response) {
        
        return getErrorResponse(new NotImplementedException(TEXT_METHOD_NOT_IMPLEMENTED, null));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody INPUT_ENTITY_TYPE entity, @PathVariable("id") String id, 
            @RequestHeader HttpHeaders httpHeaders, HttpServletRequest request, HttpServletResponse response) {
        
        return getErrorResponse(new NotImplementedException(TEXT_METHOD_NOT_IMPLEMENTED, null));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("id") String id, 
            @RequestHeader HttpHeaders httpHeaders, HttpServletRequest request, HttpServletResponse response) {
        
        return getErrorResponse(new NotImplementedException(TEXT_METHOD_NOT_IMPLEMENTED, null));
    }
}
