package com.networknt.light.rule.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.hazelcast.core.HazelcastInstance;
import com.networknt.light.rule.Rule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 8/25/2014.
 *
 * You don't need to check if the form is in db or not as the form should be cached
 * in memory image already while starting the server.
 *
 */
public class GetFormRule extends AbstractFormRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String id = (String)data.get("id");
        String json = getFormById(id);
        if(json != null) {
            inputMap.put("result", json);
            return true;
        } else {
            inputMap.put("result", "Form with " + id + " cannot be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
