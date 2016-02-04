package com.networknt.light.util;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Created by steve on 22/11/15.
 */
public class ServiceLocatorConfigAppTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {

    }

    @Test
    public void testGetConfigFromApp() throws Exception {
        ServiceLocator sl = ServiceLocator.getInstance();
        sl.delConfig("test");
        Map<String, Object> configMap = sl.getJsonMapConfig("test");
        Assert.assertEquals("application level config is in config folder with subfolder dev/sit/uat/prod and dev is the default if no system properties API_ENV is defined", configMap.get("key"));
    }
}
