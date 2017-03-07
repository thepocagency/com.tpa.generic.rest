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

import com.tpa.generic.rest.mongodbbased.generic.data.bean.BeanAbst;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

@Document
public class AccessToken extends BeanAbst<AccessToken> {

    private String applicationUuid;
    
    private String userUuid;
    
    private String roleValue;
    
    @Indexed(unique = true)
    private String jwt;

    public AccessToken() {
    }

    public AccessToken(String applicationUuid, String userUuid, String roleValue, String jwt) {
        this.applicationUuid = applicationUuid;
        this.userUuid = userUuid;
        this.roleValue = roleValue;
        this.jwt = jwt;
    }

    public String getApplicationUuid() {
        return applicationUuid;
    }

    public void setApplicationUuid(String applicationUuid) {
        this.applicationUuid = applicationUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getRoleValue() {
        return roleValue;
    }

    public void setRoleValue(String roleValue) {
        this.roleValue = roleValue;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
    
    public Role getRole() {
        return Role.valueOf(this.getRoleValue());
    }
}
