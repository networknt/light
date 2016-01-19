/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.light.rule.tag;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.db.AbstractDbRule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/21/2014.
 */
public abstract class AbstractTagRule extends AbstractRule implements Rule {

    static final Logger logger = LoggerFactory.getLogger(AbstractTagRule.class);

    public abstract boolean execute(Object... objects) throws Exception;

    public boolean getTagDropdown(Object... objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String host = (String) data.get("host");
        String docs = getTagDropdownDb(host);
        if (docs != null) {
            inputMap.put("result", docs);
            return true;
        } else {
            inputMap.put("result", "No record found");
            inputMap.put("responseCode", 404);
            return false;
        }
    }

    protected String getTagDropdownDb(String host) {
        String json = null;
        String sql = "SELECT FROM Tag WHERE host = ? ORDER BY tagId";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> docs = graph.getRawGraph().command(query).execute(host);
            if (docs.size() > 0) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for (ODocument doc : docs) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("label", (String) doc.field("tagId"));
                    map.put("value", (String) doc.field("tagId"));
                    list.add(map);
                }
                json = mapper.writeValueAsString(list);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }
}
