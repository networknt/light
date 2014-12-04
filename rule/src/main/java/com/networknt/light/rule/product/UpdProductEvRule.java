package com.networknt.light.rule.product;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.category.AbstractCategoryRule;

import java.util.Map;

/**
 * Created by husteve on 10/14/2014.
 */
public class UpdProductEvRule extends AbstractProductRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String productRid = (String) data.get("@rid");
        updProduct(productRid, data);
        return true;
    }
}
