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

package com.tpa.generic.tools.service.global;

import com.tpa.generic.tools.primitivetype.exception.type.ServerException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import com.tpa.generic.tools.service.global.spring.ServerSettings;
import com.tpa.generic.tools.service.global.spring.SpringSettings;
import com.tpa.generic.tools.service.exception.ErrorService;
import com.tpa.generic.tools.service.global.app.AppConfig;
import com.tpa.generic.tools.service.global.spring.AwsSnsSettings;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

@Service
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties(value = {SpringSettings.class, AwsSnsSettings.class, ServerSettings.class})
public class GlobalService {

    @Autowired
    private MongoTemplate mongoTemplate;
        
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private SpringSettings springSettings;
    
    @Autowired
    private AwsSnsSettings awsSnsSettings;
    
    @Autowired
    private ServerSettings serverSettings;
        
    @Autowired
    private ErrorService errorService;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
    
    public AppConfig getAppConfig() {
        return appConfig;
    }

    public SpringSettings getSpringSettings() {
        return springSettings;
    }

    public AwsSnsSettings getAwsSnsSettings() {
        return awsSnsSettings;
    }
    
    public ServerSettings getServerSettings() {
        return serverSettings;
    }

    public ErrorService getErrorService() {
        return errorService;
    }
    
    //

    public String getTextContentValue(String key) throws ServerException {
        return appConfig.getStringValue(key);
    }
    
    public String getTextContentValueAndReplaceContent(String propertyKey, Map<String, String> values) throws ServerException {
        return appConfig.getStringValueAndReplaceContent(propertyKey, values);
    }
}
