package com.networknt.light.rule.form;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.metadata.schema.OType;

import javax.xml.ws.Service;
import java.rmi.server.ServerCloneException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 9/4/2014.
 */
public class ImpFormRule extends AbstractFormRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("formAdmin")) {
                error = "Role owner or admin or formAdmin is required to import form";
                inputMap.put("responseCode", 403);
            } else {
                String host = (String)user.get("host");
                if(host != null) {
                    if(!host.equals(data.get("host"))) {
                        error = "User can only import form from host: " + host;
                        inputMap.put("responseCode", 403);
                    } else {
                        // Won't check if form exists or not here.
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.put("host", host);

                        eventData.put("id", data.get("id"));
                        eventData.put("action", data.get("action"));
                        eventData.put("schema", data.get("schema"));
                        eventData.put("form", data.get("form"));
                        eventData.put("modelData", data.get("modelData"));

                        eventData.put("createDate", new java.util.Date());
                        eventData.put("createUserId", user.get("userId"));
                    }
                } else {
                    // This is owner to import form, notice no host is passed in.
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);

                    eventData.put("id", data.get("id"));
                    eventData.put("action", data.get("action"));
                    eventData.put("schema", data.get("schema"));
                    eventData.put("form", data.get("form"));
                    eventData.put("modelData", data.get("modelData"));

                    eventData.put("createDate", new java.util.Date());
                    eventData.put("createUserId", user.get("userId"));
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
