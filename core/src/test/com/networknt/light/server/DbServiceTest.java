package com.networknt.light.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 21/12/14.
 */
public class DbServiceTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();

    public DbServiceTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(DbServiceTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDateFormat() throws Exception {
        String dateString = "2014-11-30T05:00:00.000Z";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            Date strToDate = format.parse(dateString);
            System.out.println(strToDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
