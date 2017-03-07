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

package com.tpa.generic.tools.primitivetype.uri;

import com.tpa.generic.rest.mongodbbased.security.data.bean.Role;
import com.tpa.generic.tools.primitivetype.exception.type.ForbiddenAccessException;
import com.tpa.generic.tools.primitivetype.exception.type.WrongInputException;
import com.tpa.generic.tools.primitivetype.list.ListUtils;
import com.tpa.generic.tools.primitivetype.string.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import org.springframework.http.HttpHeaders;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

public class RequestUtils {
    
    public static String GETALL = "GETALL";
    public static String GET = "GET";
    public static String POST = "POST";
    public static String PUT = "PUT";
    public static String DELETE = "DELETE";

    public static List<String> getAllMethods() {
        return Arrays.asList(GETALL, GET, POST, PUT, DELETE);
    }
    
    public static String getHeaderValue(HttpHeaders httpHeaders, String key, Boolean isOptional) throws WrongInputException {

        if (httpHeaders == null
                || !httpHeaders.containsKey(key)) {
            if (!isOptional) {
                throw new WrongInputException("Could not find the header key=" + StringUtils.getNullStringIfNullObject(key));
            }
            else {
                return null;
            }
        }
        
        if (httpHeaders.get(key).size() > 1) {
            throw new WrongInputException("Header key=" + StringUtils.getNullStringIfNullObject(key) + " has more than one value");
        }

        return httpHeaders.get(key).get(0);
    }
    
    public static String getCookieValue(Cookie[] cookies, String key, Boolean isOptional) throws WrongInputException {

        if (cookies == null
                || cookies.length == 0) {
            if (!isOptional) {
                throw new WrongInputException("Could not find the cookie key=" + StringUtils.getNullStringIfNullObject(key));
            }
            else {
                return null;
            }
        }
        
        for (int i = 0; i < cookies.length; i++) {
            
            Cookie cookie = cookies[i];
            
            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }

        throw new WrongInputException("Could not find the cookie key=" + StringUtils.getNullStringIfNullObject(key));
    }
    
    public static Map<String, List<Role>> initRights(Role role) throws ForbiddenAccessException {
        
        if (role != null) {
            Map<String, List<Role>> rights = new HashMap<>();

            rights.put(GETALL, Arrays.asList(role));
            rights.put(GET, Arrays.asList(role));
            rights.put(POST, Arrays.asList(role));
            rights.put(PUT, Arrays.asList(role));
            rights.put(DELETE, Arrays.asList(role));

            return rights;
        }
        
        throw new ForbiddenAccessException("Role can not be null");
    }
            
    public static Boolean checkGlobalControllerRigths(Map<String, List<Role>> rights) throws ForbiddenAccessException {
        
        if (rights == null
                || rights.isEmpty()) {
            throw new ForbiddenAccessException("Rights are not defined for this controller");
        }
        
        List<String> defaultRequiredMethods = RequestUtils.getAllMethods();
        List<String> inputMethods = new ArrayList<>(rights.keySet());

        if (!ListUtils.equalsNoOrder(defaultRequiredMethods, inputMethods)) {
            throw new ForbiddenAccessException("Rights are not valid for this controller (must contain exactly 5 keys)");
        }

        List<Role> defaultRequiredUserRoleEnums = Role.getAll();

        for (Map.Entry<String, List<Role>> currentRight : rights.entrySet()) {

            String method = currentRight.getKey();
            List<Role> roles = currentRight.getValue();

            if (roles == null 
                    || roles.isEmpty()) {
                throw new ForbiddenAccessException("Roles can not be empty for the method " + method);
            }

            if (Collections.disjoint(defaultRequiredUserRoleEnums, roles)) {
                throw new ForbiddenAccessException("Roles must contain at least one valid role value for the method " + method);
            }
        }
        
        return Boolean.TRUE;
    }
}
