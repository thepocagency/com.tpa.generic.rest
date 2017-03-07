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

package com.tpa.generic.rest.mongodbbased.security.data.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Role {

    THE_DUDE(0, "THE_DUDE", "Super administrator", false),
    
    COMPANY_ADMIN(1, "COMPANY_ADMIN", "Company administrator", false),
    GROUP_ADMIN(2, "GROUP_ADMIN", "Application group administrator", false),
    
    APP_ADMIN(3, "APP_ADMIN", "Application administrator", false),
    APP_USER(4, "APP_USER", "User", false),
    
    VISITOR(5, "VISITOR", "Visitor", true),
    FORBIDDEN(6, "FORBIDDEN", null, null);

    private final Integer level;
    
    private final String value;
    
    private final String frontValue;
    
    private final Boolean frontSelected;

    private Role(Integer level, String value, String frontValue, Boolean frontSelected) {
        this.level = level;
        this.value = value;
        
        this.frontValue = frontValue;
        this.frontSelected = frontSelected;
    }
    
    public Integer getLevel() {
        return this.level;
    }

    public String getValue() {
        return this.value;
    }

    public String getFrontValue() {
        return frontValue;
    }

    public Boolean isFrontSelected() {
        return frontSelected;
    }
    
    public Boolean check() {
        return this != VISITOR && this != FORBIDDEN;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
    public Boolean isPhoneNumberRequired() {
        return this.getLevel() <= Role.APP_ADMIN.getLevel();
    }
    
    public static List<Role> getAll() {
        return Arrays.<Role>asList(THE_DUDE, COMPANY_ADMIN, GROUP_ADMIN, APP_ADMIN, APP_USER, VISITOR, FORBIDDEN);
    }
    
    public static List<Role> getAllPossibles() {
        return Arrays.<Role>asList(THE_DUDE, COMPANY_ADMIN, GROUP_ADMIN, APP_ADMIN, APP_USER);
    }
    
    public static List<Role> getLowerRoles(Role role) {
        List<Role> lowerRoles = new ArrayList<>();
        
        for (Role currentRole : Role.getAllPossibles()) {
            if (currentRole.getLevel() >= role.getLevel()) {
                lowerRoles.add(currentRole);
            }
        }
        
        return lowerRoles;
    }
    
    public class RoleComparator implements Comparator<Role> {
        @Override
        public int compare(Role o1, Role o2) {
            return o1.getLevel().compareTo(o2.getLevel());
        }
    }
}
