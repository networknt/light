package com.networknt.light.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by husteve on 8/1/14.
 */
public class ServiceLocator {

    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> serverMap = null;
    Map<String, Object> hostMap = null;
    Map<String, Object> injectorEnvMap = null;

    Map<String, Map<String, Object>> memoryImage = new ConcurrentHashMap<String,Map<String, Object>>(10, 0.9f, 1);

    // This eager initialization. Assume service locator is always used.
    private static final ServiceLocator instance = new ServiceLocator();
    private ServiceLocator() {}
    public static ServiceLocator getInstance() {
        return instance;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }


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
        return serverMap.get("dburl");
    }

    public ODatabaseDocumentTx getDb() {
        loadServerConfig();
        String dburl = serverMap.get("dburl");
        String dbuser = serverMap.get("dbuser");
        String dbpass = serverMap.get("dbpass");
        return ODatabaseDocumentPool.global().acquire(dburl, dbuser, dbpass);
    }

    public Map<String, Object> getHostMap() {
        loadHostConfig();
        return hostMap;
    }

    public Map<String, Object> getInjectorEnvMap() {
        loadInjectorEnvConfig();
        return injectorEnvMap;
    }

    public Map<String, Object> getMemoryImage(String key) {
        Map<String, Object> valueMap = memoryImage.get(key);
        if(valueMap == null) {
            valueMap = new ConcurrentHashMap<String, Object>(10, 0.9f, 1);
            memoryImage.put(key, valueMap);
        }
        return valueMap;
    }

    private void loadServerConfig() {
        if(serverMap == null) {
            synchronized (ServiceLocator.class) {
                if(serverMap == null) {
                    try {
                        serverMap = mapper.readValue(new File("/home/server.json"),
                                new TypeReference<HashMap<String, String>>() {
                                });
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
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
                        hostMap = mapper.readValue(new File("/home/virtualhost.json"),
                                new TypeReference<HashMap<String, Object>>() {
                                });
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        }
    }

    private void loadInjectorEnvConfig() {
        if(injectorEnvMap == null) {
            synchronized (ServiceLocator.class) {
                if(injectorEnvMap == null) {
                    try {
                        injectorEnvMap = mapper.readValue(new File("/home/injectorenv.json"),
                                new TypeReference<HashMap<String, Object>>() {
                                });
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        }
    }

}
