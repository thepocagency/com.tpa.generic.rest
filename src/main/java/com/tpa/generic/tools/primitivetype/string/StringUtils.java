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

package com.tpa.generic.tools.primitivetype.string;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Alexandre Veremme @ The POC Agency | alex [at] the-poc-agency.com
 */

public class StringUtils {
    
    public static Boolean isUnvalidString(String str) {
        return str == null || str.isEmpty();
    }
    
    public static String getNullStringIfNullObject(String str) {
        return str != null ? str : "null";
    }

    public static String getFirstChar(String string) {

        if (string != null
                && string.length() > 1) {

            return string.substring(0);
        }

        return null;
    }

    public static String getLastChar(String string) {

        if (string != null
                && string.length() > 1) {

            return string.substring(string.length() - 1);
        }

        return null;
    }

    public static String removeAllSpecialChar(String string) {

        if (string != null
                && !string.isEmpty()) {

            return string.replaceAll("[^a-zA-Z]+","");
        }

        return null;
    }
    
    public static String removeFirstChars(String string, int startIndex) {

        if (string != null
                && !string.isEmpty()
                && startIndex > 0) {

            return string.substring(startIndex, string.length());
        }

        return null;
    }
    
    public static List<String> splitString(String str, String spliter) {
        
        if (str != null && !str.isEmpty() 
                && spliter != null && !spliter.isEmpty()) {
            
            if (str.indexOf(spliter) > 0) {
                return Arrays.asList(str.split(spliter));
            }
            else {
                return Arrays.asList(str);
            }
        }
        
        return null;
    }
}
