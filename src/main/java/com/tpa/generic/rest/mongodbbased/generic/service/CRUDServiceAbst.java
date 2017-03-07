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

package com.tpa.generic.rest.mongodbbased.generic.service;

import com.tpa.generic.rest.mongodbbased.generic.data.bean.BeanAbst;
import com.tpa.generic.rest.mongodbbased.generic.data.repository.RepositoryInt;
import com.tpa.generic.tools.primitivetype.exception.type.NotFoundException;
import com.tpa.generic.tools.primitivetype.exception.type.SavingException;
import com.tpa.generic.tools.primitivetype.exception.type.WrongInputException;
import com.tpa.generic.tools.service.global.GlobalService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

public abstract class CRUDServiceAbst<ENTITY_TYPE extends BeanAbst, REPOSITORY_TYPE extends RepositoryInt> {

    protected static Logger logger = LogManager.getLogger();
    
    private final Class<ENTITY_TYPE> entityType;
    private final RepositoryInt<ENTITY_TYPE> repository;
    
    private final GlobalService globalService;

    public CRUDServiceAbst(Class<ENTITY_TYPE> entityType, RepositoryInt<ENTITY_TYPE> repository, GlobalService globalService) {
        this.entityType = entityType;
        this.repository = repository;
        this.globalService = globalService;
    }
    
    protected REPOSITORY_TYPE getRepository() {
        return (REPOSITORY_TYPE) repository;
    }
    
    protected GlobalService getGlobalService() {
        return globalService;
    }
    
    public MongoTemplate getMongoTemplate() {
        return this.getGlobalService().getMongoTemplate();
    }
       
    public String getTypeSimpleName() {
        return entityType.getSimpleName();
    }
    
    public String getIdString(String id) {
        return id != null ? id : "null";
    }
    
    public String getEntityString(Object entity) {
        return entity != null ? entity.toString() : "null";
    }

    public List<ENTITY_TYPE> findAll() throws NotFoundException {
        
        try {
            List<ENTITY_TYPE> entities = repository.findAll();

            if (entities != null && !entities.isEmpty()) {
                return entities;
            }
        }
        catch (Exception e) {
            throw new NotFoundException("Could not find any entity of type " + getTypeSimpleName(), e);
        }
        
        throw new NotFoundException("Could not find any entity of type " + getTypeSimpleName());
    }

    public ENTITY_TYPE findOne(String id) throws NotFoundException {
        
        try {
            ENTITY_TYPE entity = (ENTITY_TYPE) repository.findOne(id);

            if (entity != null) {

                return entity;
            }
        }
        catch (Exception e) {
            throw new NotFoundException("Could not find the entity of type " + getTypeSimpleName() + " with the id=" + getIdString(id), e);
        }
        
        throw new NotFoundException("Could not find the entity of type " + getTypeSimpleName() + " with the id=" + getIdString(id));
    }
    
    public final ENTITY_TYPE simpleSave(ENTITY_TYPE entity) throws WrongInputException, SavingException {
        try {
            if (entity != null) {

                if (entity.processSpecificProperties()) {

                    if (entity.getId() != null
                            && !entity.getId().isEmpty()) {

                        entity.updateDates();
                    } else {

                        entity.createDates();
                    }

                    return (ENTITY_TYPE) repository.save((ENTITY_TYPE) entity);
                }
            }
        }
        catch (Exception e) {
            throw new SavingException("Could not save the entity with type=" + getTypeSimpleName() + " and properties=" + getEntityString(entity), e);
        }
                
        throw new SavingException("Could not save the entity with type=" + getTypeSimpleName() + " and properties=" + getEntityString(entity));
    }

    public ENTITY_TYPE save(ENTITY_TYPE entity) throws WrongInputException, SavingException {
        return this.simpleSave(entity);
    }

    @SuppressWarnings("UseSpecificCatch")
    public final ENTITY_TYPE simpleUpdate(ENTITY_TYPE entity, String id) throws WrongInputException, NotFoundException, SavingException {
        try {
            ENTITY_TYPE entityFromDb = (ENTITY_TYPE) findOne(id);

            if (entityFromDb != null) {

                ((ENTITY_TYPE) entityFromDb).setInputProperties((ENTITY_TYPE) entity);

                if (((ENTITY_TYPE) entityFromDb).processSpecificProperties()) {

                    return (ENTITY_TYPE) simpleSave((ENTITY_TYPE) entityFromDb);
                }
            }
        }
        catch (NotFoundException | SavingException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SavingException("Could not update the entity with type=" + getTypeSimpleName() + " and id=" + getIdString(id) + " with properties=" + getEntityString(entity), e);
        }

        throw new SavingException("Could not update the entity with type=" + getTypeSimpleName() + " and id=" + getIdString(id) + " with properties=" + getEntityString(entity));
    }
    
    public ENTITY_TYPE update(ENTITY_TYPE entity, String id) throws WrongInputException, NotFoundException, SavingException {
        return this.simpleUpdate(entity, id);
    }

    public final Boolean simpleDelete(String id) throws NotFoundException, SavingException {
        try  {
            ENTITY_TYPE entityFromDb = (ENTITY_TYPE) findOne(id);

            if (entityFromDb != null) {
                repository.delete(id);
                return Boolean.TRUE;
            }
        }
        catch (NotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SavingException("Could not delete the entity with type=" + getTypeSimpleName() + " and id=" + getIdString(id), e);
        }
        
        throw new SavingException("Could not delete the entity with type=" + getTypeSimpleName() + " and id=" + getIdString(id));
    }
    
    public Boolean delete(String id) throws NotFoundException, SavingException {
        return this.simpleDelete(id);
    }
}
