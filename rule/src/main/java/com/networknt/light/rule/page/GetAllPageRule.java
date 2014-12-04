package com.networknt.light.rule.page;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.form.AbstractFormRule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by husteve on 10/24/2014.
 */
public class GetAllPageRule extends AbstractPageRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String host = (String)user.get("host");

        Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            formMap.put("cache", cache);
        }

        List<ODocument> pages = getAllPage(host);
        if(pages != null) {
            inputMap.put("result", OJSONWriter.listToJSON(pages, null));
            return true;
        } else {
            inputMap.put("result", "No page can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
