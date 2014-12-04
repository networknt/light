package com.networknt.light.rule.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 23/09/14.
 */
public class GetMenuRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String host = (String) data.get("host");
        String json = getMenu(host);
        if(json != null) {
            inputMap.put("result", json);
            return true;
        } else {
            inputMap.put("result", "Menu for host " + host + " cannot be found.");
            return false;
        }
    }
}
