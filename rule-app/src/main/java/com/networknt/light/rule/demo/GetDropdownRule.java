package com.networknt.light.rule.demo;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 21/02/15.
 */
public class GetDropdownRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        inputMap.put("result", "[{\"value\":\"value1\",\"label\":\"label1\"},{\"value\":\"value2\",\"label\":\"label2\"},{\"value\":\"value3\",\"label\":\"label3\"}]");
        return true;
    }

}
