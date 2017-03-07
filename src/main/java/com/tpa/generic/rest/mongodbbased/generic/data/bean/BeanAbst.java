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

package com.tpa.generic.rest.mongodbbased.generic.data.bean;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.TextScore;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

@RepositoryRestResource(exported = false)
public abstract class BeanAbst<ENTITY_TYPE extends BeanAbst> implements BeanInt<ENTITY_TYPE> {

    protected static Logger logger = LogManager.getLogger();
    
    @Id
    private String id;

    private Date createdDate;

    private Date updatedDate;
    
    @TextScore
    private Float score;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void createDates() {
        setCreatedDate(new Date());
        setUpdatedDate(new Date());
    }

    public void updateDates() {
        setUpdatedDate(new Date());
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }
    
    @Override
    public ENTITY_TYPE setInputProperties(ENTITY_TYPE entity) {

        for (Field currentField : entity.getClass().getDeclaredFields()) {
            // To access private fields...
            currentField.setAccessible(true);

            try {
                if (currentField.get(entity) != null) {
                    currentField.set(this, currentField.get(entity));
                }
            } catch (Exception e) { 
                logger.error("Can not execute the method setInputProperties() ??", e);
            }
        }

        return (ENTITY_TYPE) this;
    }

    @Override
    public Boolean processSpecificProperties() {
        return Boolean.TRUE;
    }

    @Override
    public boolean equals(Object obj) {
        return ((BeanAbst<ENTITY_TYPE>) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.id);
        return hash;
    }
}
