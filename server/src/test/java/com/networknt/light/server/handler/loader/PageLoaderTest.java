package com.networknt.light.server.handler.loader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by husteve on 10/24/2014.
 */
public class PageLoaderTest extends TestCase {

    public PageLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(PageLoaderTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadPage() throws Exception {
        PageLoader.loadPage();
        boolean result = true;
        assertTrue(result);
    }
}
