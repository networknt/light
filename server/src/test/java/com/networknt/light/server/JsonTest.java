package com.networknt.light.server;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Map;

/**
 * Test case for CharSequenceCompiler
 *
 * @author Steve Hu
 */
public class JsonTest extends TestCase {

    public JsonTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(JsonTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testF1() {
        boolean result = false;
        assertFalse(result);
    }

}
