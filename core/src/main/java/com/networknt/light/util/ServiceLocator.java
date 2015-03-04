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
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by husteve on 8/1/14.
 */
public class ServiceLocator {
    static final Logger logger = LoggerFactory.getLogger(ServiceLocator.class);

    ObjectMapper mapper = new ObjectMapper();

    Map<String, String> serverMap = null;
    Map<String, Object> hostMap = null;

    Map<String, Map<String, Object>> memoryImage = new ConcurrentHashMap<String,Map<String, Object>>(10, 0.9f, 1);

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

    public String getIp() {
        loadServerConfig();
        return serverMap.get("ip");

    }

    public String getPort() {
        loadServerConfig();
        return serverMap.get("port");
    }

    public String getOwnerId() {
        loadServerConfig();
        return serverMap.get("ownerId");
    }

    public String getOwnerEmail() {
        loadServerConfig();
        return serverMap.get("ownerEmail");
    }

    public String getOwnerPass() {
        loadServerConfig();
        return serverMap.get("ownerPass");
    }

    public String getTestId() {
        loadServerConfig();
        return serverMap.get("testId");
    }

    public String getTestEmail() {
        loadServerConfig();
        return serverMap.get("testEmail");
    }

    public String getTestPass() {
        loadServerConfig();
        return serverMap.get("testPass");
    }

    public String getDbUrl() {
        loadServerConfig();
        String dbName = serverMap.get("dbName");
        return "plocal:"+ System.getProperty("user.home") + "/" + dbName;
    }

    public String getJwtIssuer() {
        loadServerConfig();
        return serverMap.get("jwtIssuer");
    }

    public String getJwtSigningKey() {
        loadServerConfig();
        return serverMap.get("jwtSigningKey");
    }

    public String getJwtAudience() {
        loadServerConfig();
        return serverMap.get("jwtAudience");
    }

    public String getJwtTyp() {
        loadServerConfig();
        return serverMap.get("jwtTyp");
    }

    public String getJwtExpireInSecond() {
        loadServerConfig();
        return serverMap.get("jwtExpireInSecond");
    }

    public OrientGraphFactory getFactory() {
        if(factory == null) {
            factory = new OrientGraphFactory(getDbUrl()).setupPool(1,100);
        }
        return factory;
    }

    public OrientGraph getGraph() {
        return getFactory().getTx();
    }

    public Map<String, Object> getHostMap() {
        loadHostConfig();
        return hostMap;
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


    private void loadServerConfig() {
        if(serverMap == null) {
            synchronized (ServiceLocator.class) {
                if(serverMap == null) {
                    try {
                        serverMap = mapper.readValue(new File(System.getProperty("user.home") + "/server.json"),
                                new TypeReference<HashMap<String, String>>() {
                                });
                    } catch (IOException ioe) {
                        logger.error("Exception:", ioe);
                    }
                }
            }
        }
    }

    private void loadHostConfig() {
        if(hostMap == null) {
            synchronized (ServiceLocator.class) {
                if(hostMap == null) {
                    try {
                        hostMap = mapper.readValue(new File(System.getProperty("user.home") + "/virtualhost.json"),
                                new TypeReference<HashMap<String, Object>>() {
                                });
                    } catch (IOException ioe) {
                        logger.error("Exception:", ioe);
                    }
                }
            }
        }
    }
}
