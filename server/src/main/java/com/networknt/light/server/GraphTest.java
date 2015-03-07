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

    public static void main(String[] args) {

        String sql = "select @RID, id, content, out_Has as children, in_Has as parent from (traverse out('Has') from #11:0) where @class = 'Comment'";

        OrientGraphFactory factory = new OrientGraphFactory("plocal:/home/steve/lightdb").setupPool(1,10);

        OrientGraph graph = factory.getTx();
        try {
            //System.out.println(graph.getVertex("#11:0").getRecord().toJSON("rid, fetchPlan:*:-1"));

            //List<ODocument> result = graph.getRawGraph().query(
            //        new OSQLSynchQuery(sql));
            //System.out.println(OJSONWriter.listToJSON(result, "fetchPlan:children:-1"));
            System.out.println(getMenu(graph, "example"));
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

}
