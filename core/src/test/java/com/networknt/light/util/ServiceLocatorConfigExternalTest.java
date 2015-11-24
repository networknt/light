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
 * Created by steve on 22/11/15.
 */
public class ServiceLocatorConfigExternalTest extends TestCase {

    ServiceLocator sl = ServiceLocator.getInstance();
    String homeDir = System.getProperty("user.home");

    public void setUp() throws Exception {
        super.setUp();
        // write a config file into the user home directory.
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "externalized config will be in the classpath");
        sl.getMapper().writeValue(new File(homeDir + "/test.json"), map);

        // Add home directory to the classpath of the system class loader.
        addURL(new File(homeDir).toURL());
    }

    public void tearDown() throws Exception {
        // Remove the test.json from home directory
        File test = new File(homeDir + "/test.json");
        test.delete();
    }

    @Test
    public void testGetConfigFromClassPath() throws Exception {
        sl.delConfig("test");
        Map<String, Object> configMap = sl.getConfig("test");
        Assert.assertEquals("externalized config will be in the classpath", configMap.get("key"));
    }

    public void addURL(URL url) throws Exception {
        URLClassLoader classLoader
                = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class clazz= URLClassLoader.class;

        // Use reflection
        Method method= clazz.getDeclaredMethod("addURL", new Class[] { URL.class });
        method.setAccessible(true);
        method.invoke(classLoader, new Object[]{url});
    }
}
