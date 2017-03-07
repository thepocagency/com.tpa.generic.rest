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

package com.tpa.generic.tools.service.global.app;

import com.tpa.generic.tools.primitivetype.exception.type.ServerException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

@Configuration
@PropertySources({
    @PropertySource("classpath:/generic-config.properties"),
    @PropertySource(value = "classpath:/config-${spring.profiles.active}.properties", ignoreResourceNotFound = true)
})
public class AppConfig {

    @Autowired
    private Environment env;

    public Boolean getBoolValue(String key) throws ServerException {
        try {
            if (env.containsProperty(key)) {
                return Boolean.valueOf(env.getProperty(key));
            }
            return null;
        }
        catch(Exception e) {
            throw new ServerException("Can not read the property " + key);
        }
    }
    
    public Integer getIntValue(String key) throws ServerException {

        try {
            if (env.containsProperty(key)) {
                return env.getProperty(key, Integer.class);
            }
            return null;
        }
        catch(Exception e) {
            throw new ServerException("Can not read the property " + key);
        }
    }
    
    public Long getLongValue(String key) throws ServerException {

        try {
            if (env.containsProperty(key)) {
                return new Long(env.getProperty(key, Integer.class));
            }
            return null;
        }
        catch(Exception e) {
            throw new ServerException("Can not read the property " + key);
        }
    }
    
    public String getStringValue(String key) throws ServerException {

        try {
            if (env.containsProperty(key)) {
                return env.getProperty(key);
            }
            return null;
        }
        catch(Exception e) {
            throw new ServerException("Can not read the property " + key);
        }
    }

    public String getStringValueAndReplaceContent(String propertyKey, Map<String, String> values) throws ServerException {

        String propertyValue = getStringValue(propertyKey);

        try {
            if (propertyValue != null) {

                for (Map.Entry<String, String> currentEntry : values.entrySet()) {

                    propertyValue = propertyValue.replace(currentEntry.getKey(), currentEntry.getValue());
                }

                return propertyValue;
            }

            return null;
        }
        catch(Exception e) {
            throw new ServerException("Error during replacing values in property value :" + propertyValue);
        }
    }
}
