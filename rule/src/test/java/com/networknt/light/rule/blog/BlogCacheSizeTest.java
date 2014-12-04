package com.networknt.light.rule.blog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.util.ServiceLocator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 13/10/14.
 */
public class BlogCacheSizeTest extends TestCase {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    String getBlog = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlog\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":2,\"pageNo\":1}}";

    public BlogCacheSizeTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(BlogCacheSizeTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }


    public void testExecute() throws Exception {

        GetBlogRule rule = new GetBlogRule();


        System.gc();
        long before = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        rule.refreshCache("www.example.com");
        System.gc();
        long after = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        System.out.println(after - before);
        //9401976
        /*
        System.gc();
        long before = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        rule.refreshCacheMap("www.example.com");
        System.gc();
        long after = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        System.out.println(after - before);
        //9522560
        */
    }
}