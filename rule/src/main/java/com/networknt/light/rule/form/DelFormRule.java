package com.networknt.light.rule.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 8/29/2014.
 *
 */
public class DelFormRule extends AbstractFormRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("formAdmin")) {
                error = "Role owner or admin or formAdmin is required to delete form";
                inputMap.put("responseCode", 401);
            } else {
                String host = (String)user.get("host");
                if(host != null && !host.equals(data.get("host"))) {
                    error = "User can only delete form for host: " + host;
                    inputMap.put("responseCode", 401);
                } else {
                    int inputVersion = (int)data.get("@version");
                    String id = (String)data.get("id");
                    String json = getFormById(id);
                    if(json == null) {
                        error = "Form with id " + id + " cannot be found";
                        inputMap.put("responseCode", 404);
                    } else {
                        Map<String, Object> form = mapper.readValue(json,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        // check the version
                        int storedVersion = (int)form.get("@version");
                        if(inputVersion != storedVersion) {
                            inputMap.put("responseCode", 400);
                            error = "Deleting version " + inputVersion + " doesn't match stored version " + storedVersion;
                        } else {
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.put("id", form.get("id"));
                        }
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
