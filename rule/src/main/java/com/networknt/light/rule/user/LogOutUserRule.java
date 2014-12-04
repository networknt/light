package com.networknt.light.rule.user;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 9/24/2014.
 */
public class LogOutUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String error = null;

        // TODO check the token is valid and if the user exist.
        ODocument user = DbService.getODocumentByRid(rid);
        if(user == null) {
            error = "User with @rid " + rid + " cannot be found.";
            inputMap.put("responseCode", 404);
        } else {
            data.put("logOutDate", new java.util.Date());
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
