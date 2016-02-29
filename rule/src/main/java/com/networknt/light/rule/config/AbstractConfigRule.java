package com.networknt.light.rule.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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
        String error = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Vertex config = graph.getVertexByKey("Config.configId", configId);
            if(config != null) {
                error = "configId " + configId + " exists";
                inputMap.put("responseCode", 400);
            } else {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.putAll((Map<String, Object>) inputMap.get("data"));
                eventData.put("createDate", new java.util.Date());
                eventData.put("createUserId", user.get("userId"));
                // remove host
                eventData.remove("host");
                // replace properties
                String properties = (String)data.get("properties");
                if(properties != null) {
                    Map<String, Object> map = mapper.readValue(properties,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    eventData.put("properties", map);
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

    public boolean addHostConfig (Object ...objects) throws Exception {
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
                    // replace properties
                    String properties = (String)data.get("properties");
                    if(properties != null) {
                        Map<String, Object> map = mapper.readValue(properties,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        eventData.put("properties", map);
                    }
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

    public boolean addHostConfigEv(Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addHostConfigDb(data);
        return true;
    }

    protected void addConfigDb(Map<String, Object> data) throws Exception {
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

    protected void addHostConfigDb(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            OrientVertex hostConfig = graph.addVertex("class:HostConfig", data);
            createUser.addEdge("Create", hostConfig);
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
            Vertex config = DbService.getVertexByRid(graph, rid);
            if(config != null) {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                configId = config.getProperty("configId");
                eventData.put("configId", configId);
            } else {
                error = "@rid " + rid + " doesn't exist on host " + host;
                inputMap.put("responseCode", 404);
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
            Map<String, Object> configMap = ServiceLocator.getInstance().getMemoryImage("configMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)configMap.get("cache");
            if(cache != null) {
                cache.remove(configId);
            }
            return true;
        }
    }

    public boolean delHostConfig(Object ...objects) throws Exception {
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
            Map<String, Object> configMap = ServiceLocator.getInstance().getMemoryImage("configMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)configMap.get("cache");
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

    public boolean delHostConfigEv(Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        delHostConfigDb(data);
        return true;
    }

    protected void delConfigDb(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex config = graph.getVertexByKey("Config.configId", (String)data.get("configId"));
            if(config != null) {
                graph.removeVertex(config);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    protected void delHostConfigDb(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            OrientVertex config = getConfigByHostId(graph, (String)data.get("host"), (String)data.get("configId"));
            if(config != null) {
                graph.removeVertex(config);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public OrientVertex getConfigByHostId(OrientGraph graph, String host, String configId) {
        OrientVertex config = null;
        OIndex<?> hostIdIdx = graph.getRawGraph().getMetadata().getIndexManager().getIndex("configHostIdIdx");
        OCompositeKey key = new OCompositeKey(host, configId);
        OIdentifiable oid = (OIdentifiable) hostIdIdx.get(key);
        if (oid != null) {
            config = graph.getVertex(oid.getRecord());
        }
        return config;
    }

    public boolean updConfig (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String configId = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Vertex config = DbService.getVertexByRid(graph, rid);
            if(config != null) {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.putAll((Map<String, Object>)inputMap.get("data"));
                eventData.put("updateDate", new java.util.Date());
                eventData.put("updateUserId", user.get("userId"));
                configId = config.getProperty("configId");

                String properties = (String)data.get("properties");
                if(properties != null) {
                    Map<String, Object> map = mapper.readValue(properties,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    eventData.put("properties", map);
                }
            } else {
                inputMap.put("result",  "@rid " + rid + " cannot be found");
                inputMap.put("responseCode", 404);
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        Map<String, Object> configMap = ServiceLocator.getInstance().getMemoryImage("configMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)configMap.get("cache");
        if(cache != null) {
            cache.remove(configId);
        }
        return true;
    }

    public boolean updHostConfig (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String configId = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            String userHost = (String)user.get("host");
            if(userHost != null && !userHost.equals(host)) {
                inputMap.put("result", "You can only update config from host: " + host);
                inputMap.put("responseCode", 401);
                return false;
            } else {
                Vertex config = DbService.getVertexByRid(graph, rid);
                if(config != null) {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.putAll((Map<String, Object>)inputMap.get("data"));
                    eventData.put("updateDate", new java.util.Date());
                    eventData.put("updateUserId", user.get("userId"));
                    configId = config.getProperty("configId");

                    String properties = (String)data.get("properties");
                    if(properties != null) {
                        Map<String, Object> map = mapper.readValue(properties,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        eventData.put("properties", map);
                    }
                } else {
                    inputMap.put("result",  "@rid " + rid + " cannot be found");
                    inputMap.put("responseCode", 404);
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        Map<String, Object> configMap = ServiceLocator.getInstance().getMemoryImage("configMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)configMap.get("cache");
        if(cache != null) {
            cache.remove(host + configId);
        }
        return true;
    }

    public boolean updConfigEv (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updConfigDb(data);
        return true;
    }

    public boolean updHostConfigEv (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updHostConfigDb(data);
        return true;
    }

    protected void updConfigDb(Map<String, Object> data) throws Exception {
        String configId = (String)data.get("configId");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex updateUser = graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            Vertex config = graph.getVertexByKey("Config.configId", configId);
            if(config != null) {
                if(data.get("description") != null) {
                    config.setProperty("description", data.get("description"));
                } else {
                    config.removeProperty("description");
                }
                if(data.get("properties") != null) {
                    config.setProperty("properties", data.get("properties"));
                } else {
                    config.removeProperty("properties");
                }
                config.setProperty("updateDate", data.get("updateDate"));
                // updateUser
                updateUser.addEdge("Update", config);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    protected void updHostConfigDb(Map<String, Object> data) throws Exception {
        String host = (String)data.get("host");
        String configId = (String)data.get("configId");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex updateUser = graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            OrientVertex config = getConfigByHostId(graph, host, configId);
            if(config != null) {
                if(data.get("description") != null) {
                    config.setProperty("description", data.get("description"));
                } else {
                    config.removeProperty("description");
                }
                if(data.get("properties") != null) {
                    config.setProperty("properties", data.get("properties"));
                } else {
                    config.removeProperty("properties");
                }
                config.setProperty("updateDate", data.get("updateDate"));
                // updateUser
                updateUser.addEdge("Update", config);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public String getConfig(String host, String configId) throws Exception {
        String json  = null;
        Map<String, Object> configMap = ServiceLocator.getInstance().getMemoryImage("configMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)configMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            configMap.put("cache", cache);
        } else {
            json = (String)cache.get(host + configId);
            if(json == null) {
                // fall back to system config instead of host config
                json = (String) cache.get(configId);
            }
        }
        if(json == null) {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                OrientVertex config = getConfigByHostId(graph, host, configId);
                if(config != null) {
                    json = config.getRecord().toJSON();
                    cache.put(host + configId, json);
                } else {
                    // get system config here.
                    config = (OrientVertex)graph.getVertexByKey("Config.configId", configId);
                    if(config != null) {
                        json = config.getRecord().toJSON();
                        cache.put(configId, json);
                    }
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

    protected String getAllConfig() {
        String sql = "SELECT FROM Config";
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> configs = graph.getRawGraph().command(query).execute();
            if(configs != null && configs.size() > 0) {
                json = OJSONWriter.listToJSON(configs, null);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }

    protected String getAllHostConfig(String host) {
        String sql = "SELECT FROM HostConfig WHERE host = ?";
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> configs = graph.getRawGraph().command(query).execute(host);
            if(configs != null && configs.size() > 0) {
                json = OJSONWriter.listToJSON(configs, null);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }

}
