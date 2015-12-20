package com.networknt.light.rule.shipping;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 13/12/15.
 *
 * An event rule to update user profile with new shipping address.
 *
 */
public class UpdAddressEvRule extends AbstractAddressRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updAddress(data);
        return true;
    }

}
