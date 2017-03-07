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

import com.tpa.generic.tools.primitivetype.exception.type.ServerException;
import com.tpa.generic.tools.service.global.app.AppConfig;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestConfiguration implements Filter {
    
    protected static Logger logger = LogManager.getLogger();

    @Autowired
    private AppConfig appConfig;
    
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        response.setHeader("Access-Control-Allow-Credentials", "true"); // This is alos required for the cookies (and e.g. $cookies angular lib)
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "OPTIONS, POST, PUT, GET, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        try {
            String allowedheaders = "Content-Type, "
                    + appConfig.getStringValue("generic.security.httpHeaders.jwtHeaderName") + ", "
                    + appConfig.getStringValue("generic.security.httpHeaders.appUuidHeaderName");
            
            response.setHeader("Access-Control-Allow-Headers", allowedheaders);
        } catch (ServerException ex) {
            logger.error("Error during reading property values, Server can not accept specific headers.");
        }
        
        if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }
}
