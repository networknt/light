package com.networknt.light.rule.example.main.perf;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by steve on 02/02/15.
 */
public class HelloWorldRule extends AbstractRule implements Rule {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HelloWorldRule.class);
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        logger.debug(this.toString());
        inputMap.put("result", "{\"message\": \"Hello World\"}");
        return true;
    }
}
