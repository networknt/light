package com.networknt.light.rule.form;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 9/4/2014.
 * when importing form, it might be existing or new. need to handle two situations.
 *
 */
public class ImpFormEvRule extends AbstractFormRule implements Rule {
    public boolean execute(Object... objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        String id = (String) data.get("id");
        String json = getFormById(id);
        if(json != null) {
            updForm(data);
        } else {
            addForm(data);
        }
        return true;
    }
}