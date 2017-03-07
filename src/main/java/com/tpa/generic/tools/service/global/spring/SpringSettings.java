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

package com.tpa.generic.tools.service.global.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

@Component
@ConfigurationProperties(prefix = "spring")
public class SpringSettings {

    public static String PROFILE_DEV = "dev";
    public static String PROFILE_STAGE = "stage";
    public static String PROFILE_PROD = "prod";

    private SpringMail mail;

    private String profiles;

    public SpringMail getMail() {
        return mail;
    }

    public void setMail(SpringMail mail) {
        this.mail = mail;
    }

    public String getProfiles() {
        return profiles;
    }

    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    public Boolean isDevProfile() {
        return profiles.equals(PROFILE_DEV);
    }

    public Boolean isStageProfile() {
        return profiles.equals(PROFILE_STAGE);
    }

    public Boolean isProdProfile() {
        return profiles.equals(PROFILE_PROD);
    }
}
