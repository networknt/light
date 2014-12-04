package com.networknt.light.rule.form;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddFormRule extends AbstractFormRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String id = (String)data.get("id");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("formAdmin")) {
                error = "Role owner or admin or formAdmin is required to add form";
                inputMap.put("responseCode", 401);
            } else {
                String host = (String)user.get("host");
                if(host != null) {
                    if(!host.equals(data.get("host"))) {
                        error = "User can only add form from host: " + host;
                        inputMap.put("responseCode", 401);
                    } else {
                        String json = getFormById((String)data.get("id"));
                        if(json != null) {
                            error = "Form with the same id exists";
                            inputMap.put("responseCode", 400);
                        } else {
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.putAll((Map<String, Object>)inputMap.get("data"));
                            eventData.put("createDate", new java.util.Date());
                            eventData.put("createUserId", user.get("userId"));
                        }
                    }
                } else {
                    String json = getFormById((String)data.get("id"));
                    if(json != null) {
                        error = "Form with the same id exists";
                        inputMap.put("responseCode", 400);
                    } else {
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.putAll((Map<String, Object>)inputMap.get("data"));
                        eventData.put("createDate", new java.util.Date());
                        eventData.put("createUserId", user.get("userId"));
                        // remove host from data as this is owner adding role
                        eventData.remove("host");
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
