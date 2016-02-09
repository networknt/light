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

package com.networknt.light.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import net.engio.mbassy.bus.MBassador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by husteve on 8/1/14.
 */
public class ServiceLocator {

    static final String AUDIT_LOGGER = "Audit";
    static final String CONFIG_EXT = ".json";
    static final String API_ENV = "API_ENV";
    static final String API_ENV_DEV = "dev";
    static final String CONFIG_FOLDER = "config/";
    static final String CONFIG_EXT_JSON = ".json";

    static final public String SERVER_CONFIG = "server";
    static final public String HOST_CONFIG = "virtualhost";

    static final XLogger logger = XLoggerFactory.getXLogger(ServiceLocator.class);
    static final Logger audit = LoggerFactory.getLogger(AUDIT_LOGGER);

    ObjectMapper mapper = new ObjectMapper();

    Map<String, Map<String, Object>> memoryImage = new ConcurrentHashMap<String,Map<String, Object>>(10, 0.9f, 1);
    Map<String, Object> configImage = new ConcurrentHashMap<String,Object>(10, 0.9f, 1);

    Map<String, MBassador<Map<String, Object>>> eventBusMap = new ConcurrentHashMap<>(10, 0.9f, 1);


    //HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();

    OrientGraphFactory factory = null;

    // This eager initialization. Assume service locator is always used.
    private static final ServiceLocator instance = new ServiceLocator();
    private ServiceLocator() {}
    public static ServiceLocator getInstance() {
        return instance;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    /*
    public HazelcastInstance getHzInstance() {
        return hzInstance;
    }
    */

    public MBassador<Map<String, Object>> getEventBus(String topic) {
        MBassador<Map<String, Object>> eventBus = eventBusMap.get(topic);
        if(eventBus == null) {
            eventBus = new MBassador<Map<String, Object>>();
            eventBusMap.put(topic, eventBus);
        }
        return eventBus;
    }

    public Map<String, Object> getHostMap() {
        return getJsonMapConfig(HOST_CONFIG);
    }

    public String getDbUrl() {
        Map<String, Object> serverConfig = getJsonMapConfig(SERVER_CONFIG);
        String dbName = (String)serverConfig.get("dbName");
        return "plocal:"+ System.getProperty("user.home") + "/" + dbName;
    }

    public OrientGraphFactory getFactory() {
        if(factory == null) {
            factory = new OrientGraphFactory(getDbUrl()).setupPool(1,100);
            OGlobalConfiguration.RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue(-1);
        }
        return factory;
    }

    public OrientGraph getGraph() {
        return getFactory().getTx();
    }

    public OrientGraphNoTx getGraphNoTx() {
        return getFactory().getNoTx();
    }

    public Map<String, Object> getMemoryImage(String key) {
        Map<String, Object> valueMap = memoryImage.get(key);
        if(valueMap == null) {
            valueMap = new ConcurrentHashMap<String, Object>(10, 0.9f, 1);
            memoryImage.put(key, valueMap);
        }
        return valueMap;
    }

    // for replay event. clear all cache for simple event replay deployment.
    public void clearMemoryImage() {
        memoryImage.clear();
    }

    /**
     * Get Object by the name of the config and class in JSON format.
     *
     * @param configName
     * @return
     */
    public Object getJsonObjectConfig(String configName, Class clazz) {
        logger.entry(configName, clazz);
        //checkCacheExpiration();
        Object config = (Object)configImage.get(configName);
        if(config == null) {
            synchronized (ServiceLocator.class) {
                config = (Object)configImage.get(configName);
                if(config == null) {
                    config = loadJsonObjectConfig(configName, clazz);
                    if(config != null) configImage.put(configName, config);
                }
            }
        }
        logger.exit(config);
        return config;
    }

    /**
     * Get configuration map by the name of the config. The file will only be loaded from file system
     * the first time.
     *
     * @param configName
     * @return
     */
    public Map<String, Object> getJsonMapConfig(String configName) {
        logger.entry(configName);
        audit.info("getConfig for {}", configName);
        Profiler profiler = new Profiler(ServiceLocator.class.getName());
        profiler.setLogger(logger);
        profiler.start("getConfig");
        Map<String, Object> config = (Map<String, Object>)configImage.get(configName);
        if(config == null) {
            synchronized (ServiceLocator.class) {
                config = (Map<String, Object>)configImage.get(configName);
                if(config == null) {
                    config = loadJsonMapConfig(configName);
                    if(config != null) configImage.put(configName, config);
                }
            }
        }
        profiler.stop().log();
        logger.exit(config);
        return config;
    }

    /**
     * Remove the loaded configuration map from in memory cache. This will be used to reload
     * configuration at middle night in case the externalized config has been changed.
     *
     * @param configName
     */
    public void delConfig(String configName) {
        logger.entry(configName);
        configImage.remove(configName);
        logger.exit();
    }

    private String loadStringFromFile(String filename) {
        String content = null;
        logger.entry(filename);
        InputStream inStream = null;
        try {
            inStream = getConfigStream(filename);
            if(inStream != null) {
                content = convertStreamToString(inStream);
            }
        } catch (Exception ioe) {
            logger.catching(ioe);
        } finally {
            if(inStream != null) {
                try {
                    inStream.close();
                } catch(IOException ioe) {
                    logger.catching(ioe);
                }
            }
        }
        logger.exit(content);
        return content;
    }

    private Object loadJsonObjectConfig(String configName, Class clazz) {
        Object config = null;
        logger.entry(configName, clazz);
        String configFilename = configName + CONFIG_EXT_JSON;
        InputStream inStream = null;
        try {
            inStream = getConfigStream(configFilename);
            if(inStream != null) {
                config = mapper.readValue(inStream, clazz);
            }
        } catch (IOException ioe) {
            logger.catching(ioe);
        } finally {
            if(inStream != null) {
                try {
                    inStream.close();
                } catch(IOException ioe) {
                    logger.catching(ioe);
                }
            }
        }
        logger.exit(config);
        return config;
    }

    private Map<String, Object> loadJsonMapConfig(String configName) {
        Map<String, Object> config = null;
        logger.entry(configName);
        String configFilename = configName + CONFIG_EXT_JSON;
        InputStream inStream = null;
        try {
            inStream = getConfigStream(configFilename);
            if(inStream != null) {
                config = mapper.readValue(inStream, new TypeReference<HashMap<String, Object>>() {});
            }
        } catch (IOException ioe) {
            logger.catching(ioe);
        } finally {
            if(inStream != null) {
                try {
                    inStream.close();
                } catch(IOException ioe) {
                    logger.catching(ioe);
                }
            }
        }
        logger.exit(config);
        return config;
    }

    private InputStream getConfigStream(String configFilename) {

        String env = System.getProperty(API_ENV, API_ENV_DEV);

        // load from system property path first. Externalized on the target deployment environment
        InputStream inStream = null;
        try{
            inStream = new FileInputStream(System.getProperty("config.dir", "") + "/" + configFilename);
        } catch (FileNotFoundException ex){
            if(logger.isInfoEnabled()) {
                logger.info("Config not found in filesystem. Trying classpath locations.");
            }
        }
        if (inStream != null) {
            if(logger.isInfoEnabled()) {
                logger.info("Config loaded from external filesystem directory for " + configFilename + " in " + System.getProperty("config.dir", ""));
            }
            return inStream;
        }
        inStream = getClass().getClassLoader().getResourceAsStream(configFilename);
        if(inStream != null) {
            if(logger.isInfoEnabled()) {
                logger.info("config loaded from classpath for " + configFilename);
            }
            return inStream;
        }
        // load from app config/dev
        inStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FOLDER + env + "/" + configFilename);
        if(inStream != null) {
            if(logger.isInfoEnabled()) {
                logger.info("config loaded from app environmental folder in resources for " + configFilename + " in " + env);
            }
            return inStream;
        }
        inStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FOLDER + configFilename);
        if(inStream != null) {
            // couldn't load it from classpath or app resource, then use the one from resource /config folder.
            if(logger.isInfoEnabled()) {
                logger.info("config loaded from module resources for " + configFilename);
            }
            return inStream;
        }
        logger.error("Unable to load config " + configFilename);
        return inStream;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
