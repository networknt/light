package com.networknt.light.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by husteve on 8/28/2014.
 */
public class Util {
    public static String getCommandRuleClass(Map<String, Object> jsonMap) throws Exception {
        Class c = Class.forName("com.networknt.light.rule.Rule");
        String commandName = (String) jsonMap.get("name");
        String ruleClass = commandName.substring(0, 1).toUpperCase() + commandName.substring(1) + "Rule";
        ruleClass = c.getPackage().getName() + "." +
                (jsonMap.get("host") == null? "" : jsonMap.get("host") + ".") +
                (jsonMap.get("app") == null? "" : jsonMap.get("app") + ".") +
                (jsonMap.get("category") == null? "" : jsonMap.get("category") + ".") +
                ruleClass;
        //System.out.println("ruleClass = " + ruleClass);
        return ruleClass;
    }

    public static String getEventRuleClass(Map<String, Object> jsonMap) throws Exception {
        Class c = Class.forName("com.networknt.light.rule.Rule");
        String eventName = (String) jsonMap.get("name");
        String ruleClass = eventName.substring(0, 1).toUpperCase() + eventName.substring(1) + "EvRule";
        ruleClass = c.getPackage().getName() + "." +
                (jsonMap.get("host") == null? "" : jsonMap.get("host") + ".") +
                (jsonMap.get("app") == null? "" : jsonMap.get("app") + ".") +
                (jsonMap.get("category") == null? "" : jsonMap.get("category") + ".") +
                ruleClass;
        //System.out.println("ruleClass = " + ruleClass);
        return ruleClass;
    }
}
