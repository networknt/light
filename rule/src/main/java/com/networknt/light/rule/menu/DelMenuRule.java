package com.networknt.light.rule.menu;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.blog.AbstractBlogRule;
import com.networknt.light.server.DbService;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/29/2014.
 */
public class DelMenuRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String rid = (String)data.get("@rid");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("menuAdmin") && !roles.contains("admin")) {
                error = "Role owner or admin or menuAdmin is required to delete menu";
                inputMap.put("responseCode", 401);
            } else {
                String host = (String)user.get("host");
                if(host != null && !host.equals(data.get("host"))) {
                    error = "User can only delete menu for host: " + host;
                    inputMap.put("responseCode", 401);
                } else {
                    ODocument menu = DbService.getODocumentByRid(rid);
                    if(menu == null) {
                        error = "Menu with @rid " + rid + " cannot be found";
                        inputMap.put("responseCode", 404);
                    } else {
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.put("host", menu.field("host"));// unique key
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
