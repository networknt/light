package com.networknt.light.rule.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2015-01-09.
 */
public class GetTestRule extends AbstractRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        inputMap.put("result", "I've got your command!");
        return true;
    }
}
