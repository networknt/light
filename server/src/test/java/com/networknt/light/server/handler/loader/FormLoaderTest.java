package com.networknt.light.server.handler.loader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by steve on 23/08/14.
 */
public class FormLoaderTest extends TestCase {

    public FormLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(FormLoaderTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    /*
    public void testGetFileList() throws Exception {
        Loader.getFileList(null);
        boolean result = true;
        assertTrue(result);
    }
    */
    public void testLoadForm() throws Exception {
        FormLoader.loadForm();
        boolean result = true;
        assertTrue(result);
    }

}
