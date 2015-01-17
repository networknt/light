package com.networknt.light.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by steve on 05/01/15.
 */
public class UtilTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();

    public UtilTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(UtilTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetUserHome() throws Exception {
        String userHome = Util.getUserHome();
        System.out.println("uesrHome = " + userHome);
    }
}
