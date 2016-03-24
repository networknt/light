package com.networknt.light.server;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.String;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by steve on 24/02/15.
 */
public class GraphTest {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(GraphTest.class);

    public static void main(String[] args) {
        String sql = "select from Comment where in_HasComment[0] = #35:22";

        //OrientGraphFactory factory = new OrientGraphFactory("plocal:/home/steve/lightdb").setupPool(1,10);
        OrientGraphFactory factory = new OrientGraphFactory("plocal:/Users/HUS5/lightdb").setupPool(1,10);
        OrientGraph graph = factory.getTx();
        try {
            //System.out.println(graph.getVertex("#11:0").getRecord().toJSON("rid, fetchPlan:*:-1"));

            //String result = graph.getVertex("#37:0").getRecord().toJSON("rid,fetchPlan:[*]in_Create:-2 out_HasComment:5");
            //String result = graph.getVertex("#35:22").getRecord().toJSON("rid,version,fetchPlan:out_HasComment:-1 out_HasComment.out_HasComment:-1 out_HasComment.in_Create:0");
            //String result = graph.getVertex("#35:22").getRecord().toJSON("rid,in_Create.userId, fetchPlan:out_HasComment:5");
            //System.out.println(result);
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> list = graph.getRawGraph().command(query).execute();
            String json = OJSONWriter.listToJSON(list, "rid,fetchPlan:[*]in_HasComment:-2 in_Create[]:0 [*]out_Create:-2 [*]out_Update:-2 [*]out_HasComment:-1");
            System.out.println(json);
            //System.out.println(getBfnTree("forum", "example"));
        } finally {
            graph.shutdown();
        }
    }

    protected static String getMenu(OrientGraph graph, String host) {
        String json = null;
        Map<String, Object> menuMap = (Map<String, Object>) ServiceLocator.getInstance().getMemoryImage("menuMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)menuMap.get("cache");
        if(cache != null) {
            json = (String)cache.get(host);
        }
        if(json == null) {
            Vertex menu = graph.getVertexByKey("Menu.host", host);
            if(menu != null) {
                json = ((OrientVertex)menu).getRecord().toJSON("rid,fetchPlan:out_Own.in_Create:-2 out_Own:2");
            }
            if(json != null) {
                if(cache == null) {
                    cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                            .maximumWeightedCapacity(1000)
                            .build();
                    menuMap.put("cache", cache);
                }
                cache.put(host, json);
            }
        }
        return json;
    }

    protected static String getBfnTree(String bfnType, String host) {
        String json = null;
        String sql = "SELECT FROM " + bfnType + " WHERE host = ? and in_Own IS NULL ORDER BY id";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> docs = graph.getRawGraph().command(query).execute(host);
            if(docs.size() > 0) {
                json = OJSONWriter.listToJSON(docs, "rid,fetchPlan:out_Own.in_Create:-2 out_Own.out_Create:-2 out_Own:-1");
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }

}
