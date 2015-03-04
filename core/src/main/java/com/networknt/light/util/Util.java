/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.light.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

/**
 * Created by husteve on 8/28/2014.
 */
public class Util {
    static final Logger logger = LoggerFactory.getLogger(Util.class);

    public static String getCommandRuleId(Map<String, Object> jsonMap) throws Exception {
        Class c = Class.forName("com.networknt.light.rule.Rule");
        logger.debug("jsonMap = "  + jsonMap);
        String commandName = (String) jsonMap.get("name");
        // Ev rules should only be called internally not from browser.
        if(commandName.endsWith("Ev")) {
            throw new Exception("Invalid command name: " + commandName);
        }
        String ruleId = commandName.substring(0, 1).toUpperCase() + commandName.substring(1) + "Rule";
        ruleId = c.getPackage().getName() + "." +
                (jsonMap.get("host") == null? "" : jsonMap.get("host") + ".") +
                (jsonMap.get("app") == null? "" : jsonMap.get("app") + ".") +
                (jsonMap.get("category") == null? "" : jsonMap.get("category") + ".") +
                (jsonMap.get("version") == null? "" : jsonMap.get("version") + ".") +
                ruleId;
        return ruleId;
    }

    public static String getEventRuleId(Map<String, Object> jsonMap) throws Exception {
        Class c = Class.forName("com.networknt.light.rule.Rule");
        String eventName = (String) jsonMap.get("name");
        String ruleId = eventName.substring(0, 1).toUpperCase() + eventName.substring(1) + "EvRule";
        ruleId = c.getPackage().getName() + "." +
                (jsonMap.get("host") == null? "" : jsonMap.get("host") + ".") +
                (jsonMap.get("app") == null? "" : jsonMap.get("app") + ".") +
                (jsonMap.get("category") == null? "" : jsonMap.get("category") + ".") +
                (jsonMap.get("version") == null? "" : jsonMap.get("version") + ".") +
                ruleId;
        return ruleId;
    }

    public static String stacktraceToString(Exception e)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);
        ps.close();
        return baos.toString();
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }
}
