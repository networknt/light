package com.networknt.light.rule.product;

import com.networknt.light.rule.Rule;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/14/2014.
 */
public class AddProductRule extends AbstractProductRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");

        String host = (String)data.get("host");
        String name = (String)data.get("name");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            // TODO check if the user has permission to do so.

            ODocument product = getProductByHostName(host, name);
            if(product != null) {
                error = "Product with the same name exists in host" + host;
                inputMap.put("responseCode", 400);
            }
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
