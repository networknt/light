package com.networknt.light.rule.access;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.rule.AbstractRuleRule;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 30/01/15.
 */
public class GetAccessRule extends AbstractAccessRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(GetAccessRule.class);
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String ruleClass = (String)data.get("ruleClass");
        Map<String, Object> access = getAccessByRuleClass(ruleClass);
        if(access != null) {
            inputMap.put("result", mapper.writeValueAsString(access));
            return true;
        } else {
            inputMap.put("result", "No access control can be found for ruleClass" + ruleClass);
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
