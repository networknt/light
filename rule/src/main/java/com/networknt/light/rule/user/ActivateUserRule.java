package com.networknt.light.rule.user;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.Map;

/**
 * Created by steve on 26/05/15
 *
 * Although this rule update database, but it is only one time thing and we
 * don't want side effect in the future when replaying. so just do it in the
 * rule class not event rule class.
 *
 * AccessLevel A everyone.
 * .
 */
public class ActivateUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String email = (String) data.get("email");
        String code = (String)data.get("code");
        if(email != null && code != null) {
            Map eventMap = getEventMap(inputMap);
            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
            inputMap.put("eventMap", eventMap);
            eventData.putAll(data);
            inputMap.put("result", "Your account is activated.");
            return true;
        } else {
            inputMap.put("result", "email and activation code are required.");
            inputMap.put("responseCode", 400);
            return false;
        }
    }
}
