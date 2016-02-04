package com.networknt.light.util;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 2/4/2016.
 */
public class ServiceLocatorConfigPropertyPathTest extends TestCase {
    String homeDir = System.getProperty("user.home");

    public void setUp() throws Exception {
        super.setUp();
        // Add a system property here.
        System.setProperty("config.dir", homeDir);

        // write a config file into the user home directory.
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "externalized config will be in the system property path");
        ServiceLocator sl = ServiceLocator.getInstance();
        sl.getMapper().writeValue(new File(homeDir + "/test.json"), map);

    }

    public void tearDown() throws Exception {
        // Remove the test.json from home directory
        File test = new File(homeDir + "/test.json");
        test.delete();
    }

    @Test
    public void testGetConfigFromPropertyPath() throws Exception {
        ServiceLocator sl = ServiceLocator.getInstance();
        sl.delConfig("test");
        Map<String, Object> configMap = sl.getJsonMapConfig("test");
        Assert.assertEquals("externalized config will be in the system property path", configMap.get("key"));
    }

}
