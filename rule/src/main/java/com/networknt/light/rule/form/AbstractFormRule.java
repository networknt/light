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

package com.networknt.light.rule.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.RuleEngine;
import com.networknt.light.util.ServiceLocator;
import com.networknt.light.util.Util;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by steve on 23/09/14.
 */
public abstract class AbstractFormRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractFormRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    static {
        MBassador<Map<String, Object>> ruleBus = ServiceLocator.getInstance().getEventBus("rule");
        ruleBus.subscribe(new RuleMessageListenerImpl());
    }

    private static class RuleMessageListenerImpl {
        @Handler
        public void onMessage(Map<String, Object> eventMap) throws Exception {
            Map<String, Object> data = (Map<String, Object>)eventMap.get("data");
            System.out.println("Received: " + eventMap);
            // which form has a drop down of rules that depending on addRule, delRule and impRule?
            // it is defined in subscribe data for this rule AbstractFormRule. remove the form from
            // cache so that the dropdown list can be enriched again when the form is called next
            // time. The reason I don't reload the form here is because there might be so many rules
            // imported at the same time and you don't want to reload again and again. Lazy loading.
            Map map = getRuleByRuleClass(this.getClass().getName());
            Object isSubscriber = map.get("isSubscriber");
            if(isSubscriber != null && (boolean)isSubscriber) {
                Map subMap = (Map)map.get("subMap");
                List<String> formIds = (List)subMap.get("rule");
                Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
                ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
                if(cache != null) {
                    for(String formId: formIds) {
                        cache.remove(formId);
                    }
                }
            }

            // find the vertex for rule com.networknt.light.rule.rule.AbstractRuleRule and find edge
            // Depend from Form vertex.

            /*
            Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
            if(cache != null) {
                cache.remove(data.get("formId"));
            }
            */
        }
    }

    /*
    static {
        System.out.println("AbstractFromRule is called");
        ITopic<Map<String, Object>> rule = ServiceLocator.getInstance().getHzInstance().getTopic( "rule" );
        rule.addMessageListener(new RuleMessageListenerImpl());

        ITopic<Map<String, Object>> host = ServiceLocator.getInstance().getHzInstance().getTopic( "host" );
        host.addMessageListener(new HostMessageListenerImpl());

        ITopic<Map<String, Object>> role = ServiceLocator.getInstance().getHzInstance().getTopic( "role" );
        role.addMessageListener(new RoleMessageListenerImpl());

    }

    private static class RuleMessageListenerImpl implements MessageListener<Map<String, Object>> {
        @Override
        public void onMessage(final Message<Map<String, Object>> message) {
            Map<String, Object> eventMap = message.getMessageObject();
            Map<String, Object> data = (Map<String, Object>)eventMap.get("data");
            System.out.println("Received: " + eventMap);
            // do we know which form to be removed from cache? Need to define the dependency between form
            // and rule
            Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
            if(cache != null) {
                cache.remove(data.get("formId"));
            }
        }
    }

    private static class HostMessageListenerImpl implements MessageListener<Map<String, Object>> {
        @Override
        public void onMessage(final Message<Map<String, Object>> message) {
            Map<String, Object> eventMap = message.getMessageObject();
            Map<String, Object> data = (Map<String, Object>)eventMap.get("data");
            System.out.println("Received: " + eventMap);
            // simply remove the page from cache in order to reload in the next getPage rule.
            Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
            if(cache != null) {
                cache.remove(data.get("formId"));
            }
        }
    }

    private static class RoleMessageListenerImpl implements MessageListener<Map<String, Object>> {
        @Override
        public void onMessage(final Message<Map<String, Object>> message) {
            Map<String, Object> eventMap = message.getMessageObject();
            Map<String, Object> data = (Map<String, Object>)eventMap.get("data");
            System.out.println("Received: " + eventMap);
            // simply remove the page from cache in order to reload in the next getPage rule.
            Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
            if(cache != null) {
                cache.remove(data.get("formId"));
            }
        }
    }
    */
    protected String getFormById(Map<String, Object> inputMap) throws Exception {
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String formId = (String)data.get("formId");
        String json  = null;
        Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            formMap.put("cache", cache);
        } else {
            json = (String)cache.get(formId);
        }
        if(json == null) {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                OrientVertex form = (OrientVertex)graph.getVertexByKey("Form.formId", formId);
                if(form != null) {
                    json = form.getRecord().toJSON();
                    if(formId.endsWith("_d")) {
                        // enrich the form with dynamicOptions for drop down values
                        json = enrichForm(json, inputMap);
                    }
                    cache.put(formId, json);
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                graph.shutdown();
            }
        }
        return json;
    }

    protected String enrichForm(String json, Map<String, Object> inputMap)  throws Exception {
    	Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Pattern pattern = Pattern.compile("\\[\\{\"label\":\"dynamic\",([^]]+)\\}\\]");
        Matcher m = pattern.matcher(json);
        StringBuffer sb = new StringBuffer(json.length());
        while (m.find()) {
            String text = m.group(1);
            // get the values from rules.
            logger.debug("text = {}", text);
            text = text.substring(8);
            logger.debug("text = {}", text);
            Map<String, Object> jsonMap = mapper.readValue(text,
                    new TypeReference<HashMap<String, Object>>() {});
            jsonMap.put("payload", inputMap.get("payload"));
            // inject host into data here.
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("host", data.get("host"));
            jsonMap.put("data", dataMap);
            RuleEngine.getInstance().executeRule(Util.getCommandRuleId(jsonMap), jsonMap);
            String result = (String)jsonMap.get("result");
            logger.debug("result = {}", result);
            if(result != null && result.length() > 0) {
                m.appendReplacement(sb, Matcher.quoteReplacement(result));
            } else {
                m.appendReplacement(sb, Matcher.quoteReplacement("[ ]"));
            }
        }
        m.appendTail(sb);
        logger.debug("form = {}", sb.toString());
        return sb.toString();
    }

    protected String addForm(Map<String, Object> data) throws Exception {
        String json = null;
        String formId = (String)data.get("formId");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            OrientVertex form = graph.addVertex("class:Form", data);
            createUser.addEdge("Create", form);
            // According to action in the list, populate validation schema in rule class
            List<Map<String, Object>> actions = form.getProperty("action");
            for(Map<String, Object> action: actions) {
                String ruleClass = Util.getCommandRuleId(action);
                Vertex rule = graph.getVertexByKey("Rule.ruleClass", ruleClass);
                if(rule != null) {
                    rule.setProperty("schema", data.get("schema"));
                    Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
                    ConcurrentMap<String, Map<String, Object>> cache = (ConcurrentMap<String, Map<String, Object>>)ruleMap.get("cache");
                    if(cache != null) {
                        cache.remove(ruleClass);
                    }
                } else {
                    logger.error("Could not find rule " + ruleClass);
                }
            }
            graph.commit();
            json = form.getRecord().toJSON();
        } catch (Exception e) {
            graph.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
        if(cache != null) {
            cache.remove(formId);
        }
        return json;
    }

    protected void delForm(String formId) {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex form = graph.getVertexByKey("Form.formId", formId);
            if(form != null) {
                List<Map<String, Object>> actions = form.getProperty("action");
                for(Map<String, Object> action: actions) {
                    String ruleClass = Util.getCommandRuleId(action);
                    Vertex rule = graph.getVertexByKey("Rule.ruleClass", ruleClass);
                    if(rule != null) {
                        rule.removeProperty("schema");
                        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
                        ConcurrentMap<String, Map<String, Object>> cache = (ConcurrentMap<String, Map<String, Object>>)ruleMap.get("cache");
                        if(cache != null) {
                            cache.remove(ruleClass);
                        }
                    } else {
                        logger.error("Could not find rule " + ruleClass);
                    }
                }
                graph.removeVertex(form);
            }
            graph.commit();
        } catch (Exception e) {
            graph.rollback();
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
        if(cache != null) {
            cache.remove(formId);
        }
    }

    protected String updForm(Map<String, Object> data) throws Exception {
        String json = null;
        String formId = (String)data.get("formId");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            OrientVertex form = (OrientVertex)graph.getVertexByKey("Form.formId", formId);
            if(form != null) {
                form.setProperty("action", data.get("action"));
                form.setProperty("schema", data.get("schema"));
                form.setProperty("form", data.get("form"));
                form.setProperty("modelData", data.get("modelData"));
                form.setProperty("updateDate", data.get("updateDate"));

                Vertex updateUser = graph.getVertexByKey("User.userId", data.get("updateUserId"));
                updateUser.addEdge("Update", form);

                // According to action in the list, populate validation schema.
                List<Map<String, Object>> actions = form.getProperty("action");
                for(Map<String, Object> action: actions) {
                    String ruleClass = Util.getCommandRuleId(action);
                    Vertex rule = graph.getVertexByKey("Rule.ruleClass", ruleClass);
                    if(rule != null) {
                        rule.setProperty("schema", data.get("schema"));
                        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
                        ConcurrentMap<String, Map<String, Object>> cache = (ConcurrentMap<String, Map<String, Object>>)ruleMap.get("cache");
                        if(cache != null) {
                            cache.remove(ruleClass);
                        }
                    } else {
                        logger.error("Could not find rule " + ruleClass);
                    }
                }
            }
            graph.commit();
            json = form.getRecord().toJSON();
        } catch (Exception e) {
            graph.rollback();
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
        if(cache != null) {
            cache.remove(formId);
        }
        return json;
    }

    protected String impForm(Map<String, Object> data) throws Exception {
        String json = null;
        String formId = (String)data.get("formId");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            OrientVertex form = (OrientVertex)graph.getVertexByKey("Form.formId", formId);
            if(form != null) {
                graph.removeVertex(form);
            }

            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            form = graph.addVertex("class:Form", data);
            createUser.addEdge("Create", form);
            // According to action in the list, populate validation schema.
            List<Map<String, Object>> actions = form.getProperty("action");
            for(Map<String, Object> action: actions) {
                String ruleClass = Util.getCommandRuleId(action);
                Vertex rule = graph.getVertexByKey("Rule.ruleClass", ruleClass);
                if(rule != null) {
                    rule.setProperty("schema", data.get("schema"));
                    Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
                    ConcurrentMap<String, Map<String, Object>> cache = (ConcurrentMap<String, Map<String, Object>>)ruleMap.get("cache");
                    if(cache != null) {
                        cache.remove(ruleClass);
                    }
                } else {
                    logger.error("Could not find rule " + ruleClass);
                }
            }
            graph.commit();
            json = form.getRecord().toJSON();
        } catch (Exception e) {
            graph.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
        if(cache != null) {
            cache.remove(formId);
        }
        return json;
    }

    protected String getAllForm(String host) {
        String sql = "SELECT FROM Form";
        if(host != null) {
            sql = sql + " WHERE host = '" + host + "' OR host IS NULL";
        }
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> forms = graph.getRawGraph().command(query).execute();
            if(forms != null && forms.size() > 0) {
                json = OJSONWriter.listToJSON(forms, null);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }

    protected String getFormMap(String host) throws Exception {
        String sql = "SELECT FROM Form";
        if(host != null) {
            sql = sql + " WHERE host = '" + host + "' OR host IS NULL";
        }
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Map<String, Map<String, Object>> formMap = new HashMap<String, Map<String, Object>>();
            for (Vertex v : (Iterable<Vertex>) graph.command(
                    new OCommandSQL(sql)).execute()) {
                Map<String, Object> contentMap = new HashMap<String, Object>();
                contentMap.put("action", v.getProperty("action"));
                contentMap.put("schema", v.getProperty("schema"));
                contentMap.put("form", v.getProperty("form"));
                contentMap.put("modelData", v.getProperty("modelData"));
                formMap.put(v.getProperty("formId"), contentMap);
            }
            json = mapper.writeValueAsString(formMap);
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }

}
