package com.networknt.light.server.handler.loader;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by steve on 24/08/14.
 */
public class TemplateLoaderTest extends TestCase {
    public TemplateLoaderTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TemplateLoaderTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLoadTemplateOriental() throws Exception {
        TemplateLoader.loadTemplateOriental();
        boolean result = true;
        assertTrue(result);
    }


}
