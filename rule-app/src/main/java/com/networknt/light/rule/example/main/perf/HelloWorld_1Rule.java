package com.networknt.light.rule.example.main.perf;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by steve on 16/02/15.
 */
public class HelloWorld_1Rule extends AbstractRule implements Rule {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HelloWorldRule.class);
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        logger.debug(this.toString());
        inputMap.put("result", "{\"message\": \"Hello World 1\"}");
        return true;
    }
}
