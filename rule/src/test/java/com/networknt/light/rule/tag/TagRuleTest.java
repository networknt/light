package com.networknt.light.rule.tag;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by steve on 31/01/16.
 */
public class TagRuleTest extends TestCase {

    public TagRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TagRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetTagEntityListDb() throws Exception {
        String host = "www.edibleforestgarden.ca";
        String tagId = "nitrogen fixer";
        GetTagEntityRule getTagEntityRule = new GetTagEntityRule();
        List<String> list = getTagEntityRule.getTagEntityListDb(host, tagId);
        System.out.println("list = " + list);
    }

    public void testGetCategoryEntityDb() throws Exception {
        String entityRid = "#37:16";
        GetTagEntityRule getTagEntityRule = new GetTagEntityRule();
        Map<String, Object> jsonMap = getTagEntityRule.getCategoryEntityDb(entityRid);
        System.out.println(jsonMap);
    }
}
