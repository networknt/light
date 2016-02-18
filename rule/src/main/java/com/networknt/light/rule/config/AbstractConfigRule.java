package com.networknt.light.rule.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 2/18/2016.
 */
public abstract class AbstractConfigRule extends AbstractRule implements Rule {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    static final Logger logger = LoggerFactory.getLogger(AbstractConfigRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    public boolean addConfig (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String configId = (String) data.get("configId");
        String host = (String) data.get("host");
        String error = null;
        String userHost = (String)user.get("host");
        if(userHost != null && !userHost.equals(host)) {
            error = "You can only add config from host: " + host;
            inputMap.put("responseCode", 401);
        } else {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                ODocument config = getODocumentByHostId(graph, "configHostIdIdx", host, configId);
                if(config != null) {
                    error = "configId " + configId + " exists on host " + host;
                    inputMap.put("responseCode", 400);
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.putAll((Map<String, Object>) inputMap.get("data"));
                    eventData.put("createDate", new java.util.Date());
                    eventData.put("createUserId", user.get("userId"));
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                graph.shutdown();
            }
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            // no need to clean the cache here.
            /*
            Map<String, Object> configMap = ServiceLocator.getInstance().getMemoryImage("configMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)configMap.get("cache");
            if(cache != null) {
                cache.remove(host + configId);
            }
            */
            return true;
        }
    }

    public boolean addConfigEv(Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addConfigDb(data);
        return true;
    }

    protected void addConfigDb(Map<String, Object> data) throws Exception {
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            OrientVertex config = graph.addVertex("class:Config", data);
            createUser.addEdge("Create", config);
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public boolean delConfig(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String configId = null;
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            String userHost = (String)user.get("host");
            if(userHost != null && !userHost.equals(host)) {
                error = "You can only delete config from host: " + host;
                inputMap.put("responseCode", 401);
            } else {
                Vertex config = DbService.getVertexByRid(graph, rid);
                if(config != null) {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("host", host);
                    configId = config.getProperty("configId");
                    eventData.put("configId", configId);
                } else {
                    error = "@rid " + rid + " doesn't exist on host " + host;
                    inputMap.put("responseCode", 404);
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            // update the branch tree as one of branch has changed.
            Map<String, Object> branchMap = ServiceLocator.getInstance().getMemoryImage("configMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)branchMap.get("cache");
            if(cache != null) {
                cache.remove(host + configId);
            }
            return true;
        }
    }

    public boolean delConfigEv(Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        delConfigDb(data);
        return true;
    }

    protected void delConfigDb(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            OrientVertex config = getBranchByHostId(graph, branchType, (String)data.get("host"), (String)data.get("categoryId"));
            if(branch != null) {
                graph.removeVertex(branch);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }


}
