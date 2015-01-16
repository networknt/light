package com.networknt.light.rule.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.RuleEngine;
import com.networknt.light.util.Util;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by steve on 8/25/2014.
 *
 * You don't need to check if the form is in db or not as the form should be cached
 * in memory image already while starting the server.
 *
 */
public class GetFormRule extends AbstractFormRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(GetFormRule.class);

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String id = (String)data.get("id");
        String json = getFormById(id);
        if(json != null) {
            // check if the form needs dynamically load dropdown list.
            if(id.endsWith("_d")) {
                Pattern pattern = Pattern.compile("\\[\"@\",([^]]+)\\]");
                Matcher m = pattern.matcher(json);
                StringBuffer sb = new StringBuffer(json.length());
                while (m.find()) {
                    String text = m.group(1);
                    // get the values from rules.
                    logger.debug("text = {}", text);
                    Map<String, Object> jsonMap = mapper.readValue(text,
                            new TypeReference<HashMap<String, Object>>() {});
                    RuleEngine.getInstance().executeRule(Util.getCommandRuleId(jsonMap), jsonMap);
                    String result = (String)jsonMap.get("result");
                    m.appendReplacement(sb, Matcher.quoteReplacement(result));
                }
                m.appendTail(sb);
                logger.debug("form = {}", sb.toString());
                inputMap.put("result", sb.toString());
            } else {
                inputMap.put("result", json);
            }
            return true;
        } else {
            inputMap.put("result", "Form with " + id + " cannot be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
