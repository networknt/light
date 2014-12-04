package com.networknt.light.rule.page;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.form.AbstractFormRule;
import com.networknt.light.util.ServiceLocator;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by husteve on 10/24/2014.
 */
public class AddPageEvRule extends AbstractPageRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addPage(data);
        return true;
    }
}
