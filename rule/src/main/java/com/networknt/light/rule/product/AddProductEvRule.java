package com.networknt.light.rule.product;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.category.AbstractCategoryRule;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/14/2014.
 */
public class AddProductEvRule extends AbstractProductRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String userId = (String)user.get("userId");
        addProduct(data, userId);
        return true;
    }
}
