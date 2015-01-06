package com.networknt.light.util;

import java.io.*;
import java.util.Map;

/**
 * Created by husteve on 8/28/2014.
 */
public class Util {
    public static String getCommandRuleId(Map<String, Object> jsonMap) throws Exception {
        Class c = Class.forName("com.networknt.light.rule.Rule");
        String commandName = (String) jsonMap.get("name");
        String ruleId = commandName.substring(0, 1).toUpperCase() + commandName.substring(1) + "Rule";
        ruleId = c.getPackage().getName() + "." +
                (jsonMap.get("host") == null? "" : jsonMap.get("host") + ".") +
                (jsonMap.get("app") == null? "" : jsonMap.get("app") + ".") +
                (jsonMap.get("category") == null? "" : jsonMap.get("category") + ".") +
                (jsonMap.get("version")) == null? "" : jsonMap.get("version") + "." +
                ruleId;
        System.out.println("ruleId = " + ruleId);
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
                (jsonMap.get("version")) == null? "" : jsonMap.get("version") + "." +
                ruleId;
        System.out.println("ruleId = " + ruleId);
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
