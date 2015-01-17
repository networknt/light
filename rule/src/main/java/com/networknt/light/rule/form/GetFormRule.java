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
        String host = (String)data.get("host");
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
                    jsonMap.put("payload", inputMap.get("payload"));
                    // inject host into data here.
                    Map<String, Object> dataMap = new HashMap<String, Object>();
                    dataMap.put("host", host);
                    jsonMap.put("data", dataMap);
                    RuleEngine.getInstance().executeRule(Util.getCommandRuleId(jsonMap), jsonMap);
                    String result = (String)jsonMap.get("result");
                    logger.debug("result = {}", result);
                    if(result != null && result.length() > 0) {
                        m.appendReplacement(sb, Matcher.quoteReplacement(result));
                    } else {
                        m.appendReplacement(sb, Matcher.quoteReplacement("[ ]"));
                    }
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
