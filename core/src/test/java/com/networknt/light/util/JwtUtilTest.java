package com.networknt.light.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jose4j.jwt.JwtClaims;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by steve on 3/4/2016.
 */
public class JwtUtilTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();

    public JwtUtilTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(JwtUtilTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetJwt() throws Exception {
        // there is no parameter then generate a new token.
        Map<String, Object> userMap = new LinkedHashMap<String, Object>();
        userMap.put("userId", "stevehu");
        userMap.put("host", "www.networknt.com");
        userMap.put("client", "browser");
        // roles array
        ArrayList roles = new ArrayList();
        roles.add("user");
        roles.add("admin");
        userMap.put("roles", roles);
        String jwt = JwtUtil.getJwt(userMap, false);
        System.out.println("jwt = " + jwt);

        // now verify it.
        Map<String, Object> user = JwtUtil.verifyJwt(jwt);
        System.out.println("user = " + user);
    }

}
