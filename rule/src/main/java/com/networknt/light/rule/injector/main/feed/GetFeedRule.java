package com.networknt.light.rule.injector.main.feed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 9/29/2014.
 */
public class GetFeedRule extends FeedRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        long total = getTotalNumberFeed(data);
        if(total > 0) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            List<String> dataFeedTypes = getDataFeedTypes();
            if(dataFeedTypes != null) {
                result.put("dataFeedTypes", dataFeedTypes);
            }
            List<String> processTypeCds = getProcessTypeCds();
            if(processTypeCds != null) {
                result.put("processTypeCds", processTypeCds);
            }
            List<String> processSubtypeCds = getProcessSubtypeCds();
            if(processSubtypeCds != null) {
                result.put("processSubtypeCds", processSubtypeCds);
            }
            List<String> createUserIds = getCreateUserIds();
            if(createUserIds != null) {
                result.put("createUserIds", createUserIds);
            }
            List<String> updateUserIds = getUpdateUserIds();
            if(updateUserIds != null) {
                result.put("updateUserIds", updateUserIds);
            }
            String feeds = getFeed(data);
            List<Map<String, Object>> jsonList = mapper.readValue(feeds,
                    new TypeReference<List<HashMap<String, Object>>>() {
                    });
            result.put("feeds", jsonList);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            inputMap.put("error", "No feed can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
