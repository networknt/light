package com.networknt.light.rule.product;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.post.AbstractPostRule;

import java.util.Map;

/**
 * Created by steve on 02/02/16.
 *
 * Get a product from Product table in case product page is accessed by bookmark url
 *
 * AccessLevel A Anybody
 *
 */
public class GetProductRule extends AbstractProductRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String entityId = (String)data.get("entityId");
        if(entityId != null) {
            String entityRid = getEntityRid("Product", entityId);
            Map<String, Object> result = getCategoryEntity(entityRid);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            inputMap.put("result", "entityId is required.");
            inputMap.put("responseCode", 400);
            return false;
        }
    }
}
