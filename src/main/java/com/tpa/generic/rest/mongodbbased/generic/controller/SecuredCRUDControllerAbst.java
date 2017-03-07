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

import com.tpa.generic.rest.mongodbbased.generic.data.bean.BeanAbst;
import com.tpa.generic.tools.primitivetype.exception.type.ForbiddenAccessException;
import com.tpa.generic.tools.service.global.GlobalService;
import com.tpa.generic.rest.mongodbbased.generic.service.CRUDServiceAbst;
import com.tpa.generic.rest.mongodbbased.security.data.bean.Role;
import com.tpa.generic.rest.mongodbbased.security.service.GenericAccessTokenService;
import com.tpa.generic.tools.primitivetype.exception.type.NotFoundException;
import com.tpa.generic.tools.primitivetype.exception.type.SavingException;
import com.tpa.generic.tools.primitivetype.exception.type.WrongInputException;
import com.tpa.generic.tools.primitivetype.uri.RequestUtils;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 * @param <ACCESS_TOKEN_SERVICE>
 * @param <ENTITY_TYPE>
 * @param <SERVICE_TYPE> 
 */

@RestController
public abstract class SecuredCRUDControllerAbst<ACCESS_TOKEN_SERVICE extends GenericAccessTokenService, ENTITY_TYPE extends BeanAbst, SERVICE_TYPE extends CRUDServiceAbst> extends GenericSecuredControllerAbst<ACCESS_TOKEN_SERVICE, ENTITY_TYPE, SERVICE_TYPE> {

    public SecuredCRUDControllerAbst(GlobalService globalService, ACCESS_TOKEN_SERVICE accessTokenService, SERVICE_TYPE mainService) throws ForbiddenAccessException {
        super(globalService, accessTokenService, mainService, RequestUtils.initRights(Role.THE_DUDE));
    }
    
    @RequestMapping(method = RequestMethod.GET)
    @Override
    public ResponseEntity getAll(@RequestHeader HttpHeaders httpHeaders, HttpServletRequest request, HttpServletResponse response) {

        try {
            checkRigths(RequestUtils.GETALL, httpHeaders);
            
            List<ENTITY_TYPE> entities = getMainService().findAll();
            logger.debug("getAll() is returning " + entities.size() + " elements of " + getMainService().getTypeSimpleName());
            return new ResponseEntity(entities, HttpStatus.OK);
            
        } catch (NotFoundException | ForbiddenAccessException e) {
            
            logger.debug("getAll() can not find any element of " + getMainService().getTypeSimpleName());
            return getErrorResponse(e);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @Override
    public ResponseEntity get(@PathVariable("id") String id, 
            @RequestHeader HttpHeaders httpHeaders, HttpServletRequest request, HttpServletResponse response) {

        try {
            checkRigths(RequestUtils.GET, httpHeaders);
            
            ENTITY_TYPE entity = (ENTITY_TYPE) getMainService().findOne(id);
            logger.debug("get() is returning the entity " + entity.toString() + " of type " + getMainService().getTypeSimpleName());
            return new ResponseEntity(entity, HttpStatus.OK);
            
        } catch (NotFoundException | ForbiddenAccessException e) {
            
            logger.debug("get() can not find the entity " + getMainService().getTypeSimpleName() + " with the id=" + getMainService().getIdString(id));
            return getErrorResponse(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @Override
    public ResponseEntity save(@RequestBody ENTITY_TYPE entity, 
            @RequestHeader HttpHeaders httpHeaders, HttpServletRequest request, HttpServletResponse response) {

        try {
            checkRigths(RequestUtils.POST, httpHeaders);

            ENTITY_TYPE savedEntity = (ENTITY_TYPE) getMainService().save((ENTITY_TYPE) entity);
            logger.debug("save() saved the entity " + getMainService().getTypeSimpleName() + " with properties=[" + getMainService().getEntityString(entity) + "]");
            return new ResponseEntity(savedEntity, HttpStatus.OK);
            
        } catch (SavingException | WrongInputException | ForbiddenAccessException e) {
            
            logger.debug("save() can not save the entity " + getMainService().getTypeSimpleName() + " with properties=[" + getMainService().getEntityString(entity) + "]");
            return getErrorResponse(e);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @Override
    public ResponseEntity update(@RequestBody ENTITY_TYPE entity, @PathVariable("id") String id, 
            @RequestHeader HttpHeaders httpHeaders, HttpServletRequest request, HttpServletResponse response) {

        try {
            checkRigths(RequestUtils.PUT, httpHeaders);
            
            ENTITY_TYPE updatedEntity = (ENTITY_TYPE) getMainService().update(entity, id);
            logger.debug("update() updated the entity " + getMainService().getTypeSimpleName() + " with id=" + getMainService().getIdString(id) + " and with properties=[" + getMainService().getEntityString(entity) + "]");
            return new ResponseEntity(updatedEntity, HttpStatus.OK);
            
        } catch (WrongInputException | NotFoundException | SavingException | ForbiddenAccessException e) {
            
            logger.debug("update() can not update the entity " + getMainService().getTypeSimpleName() + " with id=" + getMainService().getIdString(id) + " and with properties=[" + getMainService().getEntityString(entity) + "]");
            return getErrorResponse(e);
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @Override
    public ResponseEntity delete(@PathVariable("id") String id, 
            @RequestHeader HttpHeaders httpHeaders, HttpServletRequest request, HttpServletResponse response) {

        try {
            checkRigths(RequestUtils.DELETE, httpHeaders); 
            
            getMainService().delete(id);
            logger.debug("delete() deleted the entity " + getMainService().getTypeSimpleName() + " with id=" + getMainService().getIdString(id));
            return new ResponseEntity(HttpStatus.OK);
            
        } catch (NotFoundException | SavingException| ForbiddenAccessException e) {
            
            logger.debug("delete() can not be executed for the entity " + getMainService().getTypeSimpleName() + " with id=" + getMainService().getIdString(id));
            return getErrorResponse(e);
        }
    }
}
