package com.networknt.light.util;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.ws.Service;
import java.util.Map;

/**
 * Created by steve on 22/11/15.
 */
public class ServiceLocatorConfigModuleTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("API_ENV", "prod");
    }

    public void tearDown() throws Exception {
        System.setProperty("API_ENV", "dev");
    }

    @Test
    public void testGetConfigFromApp() throws Exception {
        ServiceLocator sl = ServiceLocator.getInstance();
        sl.delConfig("test");
        Map<String, Object> configMap = sl.getJsonMapConfig("test");
        Assert.assertEquals("default module level config should be in resources/config folder", configMap.get("key"));
    }
}
