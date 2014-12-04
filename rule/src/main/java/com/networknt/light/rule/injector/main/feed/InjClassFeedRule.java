package com.networknt.light.rule.injector.main.feed;

import com.cibc.rop.data.ClassDataFeed;
import com.cibc.rop.data.IDataFeed;
import com.networknt.light.rule.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 9/5/2014.
 */
public class InjClassFeedRule extends ClassFeedRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        // make sure payload is not null. If you have payload that means the token is valid and not expired.
        String error = null;
        if(payload == null) {
            error ="Login is required";
            inputMap.put("responseCode",401);
        } else {
            // now we need to construct the bean and send to the queue.
            IDataFeed dataFeed = new ClassDataFeed();
            send(dataFeed, data);
            // data has been enriched with requestId, dataFeedType and LoanNumber etc.
            // now we need to remove all the orientdb attributes in order to save again.
            data.remove("@type");
            data.remove("@rid");
            data.remove("@version");
            data.remove("@class");
            data.remove("@fieldTypes");
            inputMap.put("result", data.get("requestId"));
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
