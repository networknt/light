package com.networknt.light.rule.category;

import com.networknt.light.rule.Rule;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/14/2014.
 */
public class UpdCategoryRule extends AbstractCategoryRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");

        String rid = (String)data.get("@rid");
        String host = (String)data.get("host");
        int inputVersion = (int)data.get("@version");

        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            // TODO check if the user has permission to do so.

            ODocument category = getCategoryByRid(rid);
            if(category == null) {
                error = "Category does not exist in host" + host;
                inputMap.put("responseCode", 404);
            } else {
                // check if the version is the same.
                int storedVersion = (int)category.field("@version");
                if(inputVersion != storedVersion) {
                    error = "Updating version " + inputVersion + " doesn't match stored version " + storedVersion;
                    inputMap.put("responseCode", 400);
                }
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
