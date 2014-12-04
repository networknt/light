package com.networknt.light.server;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;

/**
 * Created by husteve on 9/4/2014.
 */
public class InitDatabaseTest  extends TestCase {

    public InitDatabaseTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(InitDatabaseTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    /*
    public void testInitDb() {
        InitDatabase.initDb();
        System.out.println("Db schema is recreated and refreshed.");
    }
    */

    /*
    public void testUser() {
        try {
            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>("select email, displayName, roles.id from User where email = ?");
            List<ODocument> result = db.command(query).execute("stevehu@gmail.com");
            if(result.size() > 0) {
                ODocument doc = result.get(0);
                System.out.println("doc=" + doc.toJSON());
                System.out.println("doc=" + doc.toJSON("fetchPlan:roles:1"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    */

    public void testMenu() {
        try {
            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>("select from Menu where host = ?");
            List<ODocument> result = db.command(query).execute("injector");
            if(result.size() > 0) {
                ODocument doc = result.get(0);
                System.out.println("doc=" + doc.toJSON());
                System.out.println("doc=" + doc.toJSON("fetchPlan:*:2"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
