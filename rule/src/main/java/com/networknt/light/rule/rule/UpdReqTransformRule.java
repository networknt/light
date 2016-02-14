package com.networknt.light.rule.rule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 14/02/16.
 *
 * AccessLevel R [owner, admin, ruleAdmin]
 */
public class UpdReqTransformRule extends AbstractRuleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map eventMap = getEventMap(inputMap);
        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
        inputMap.put("eventMap", eventMap);
        String error = updateValidation(inputMap, eventData);
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            List<Map<String, Object>> reqTransforms = (List)data.get("reqTransforms");
            if(reqTransforms != null) {
                // convert transformData to map from string.
                for(Map<String, Object> transform: reqTransforms) {
                    String transformData = (String)transform.get("transformData");
                    if(transformData != null) {
                        Map<String, Object> map = mapper.readValue(transformData,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        transform.put("transformData", map);
                    }
                }
                eventData.put("reqTransforms", reqTransforms);
            }
            eventData.put("updateDate", new java.util.Date());
            return true;
        }
    }
}
