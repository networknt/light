package com.networknt.light.rule.category;

import com.networknt.light.rule.Rule;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/14/2014.
 *
 * you can only delete a category if it has no child and no entity.
 */
public class DelCategoryRule extends AbstractCategoryRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");

        String rid = (String)data.get("@rid");
        String host = (String)data.get("host");
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
                // check if category has children
                List children = category.field("children");
                if(children != null && children.size() > 0) {
                    error = "Category has children and cannot be deleted";
                    inputMap.put("responseCode", 400);
                } else {
                    List entities = category.field("entities");
                    if(entities != null && entities.size() > 0) {
                        error = "Category has entities and cannot be deleted";
                        inputMap.put("responseCode", 400);
                    }
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
