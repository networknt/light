package com.networknt.light.rule.address;

import com.networknt.light.rule.Rule;
import java.util.Map;

/**
 * Created by steve on 20/02/16.
 */
public class UpdShippingAddressEvRule extends AbstractAddressRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updAddress(data);
        return true;
    }
}
