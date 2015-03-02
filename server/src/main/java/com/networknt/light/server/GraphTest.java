package com.networknt.light.server;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import java.io.InputStream;
import java.lang.String;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by steve on 24/02/15.
 */
public class GraphTest {

    public static void main(String[] args) {

        String sql = "select @RID, id, content, out_Has as children, in_Has as parent from (traverse out('Has') from #11:0) where @class = 'Comment'";

        OrientGraphFactory factory = new OrientGraphFactory("plocal:/home/steve/demodb").setupPool(1,10);

        OrientGraph graph = factory.getTx();
        try {
            System.out.println(graph.getVertex("#11:0").getRecord().toJSON("rid, fetchPlan:*:-1"));

            //List<ODocument> result = graph.getRawGraph().query(
            //        new OSQLSynchQuery(sql));
            //System.out.println(OJSONWriter.listToJSON(result, "fetchPlan:children:-1"));

        } finally {
            graph.shutdown();
        }
    }
}
