/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.light.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by husteve on 8/25/2014.
 */
public class DbService {
    static final Logger logger = LoggerFactory.getLogger(DbService.class);

    public static void persistEvent(Map<String, Object> eventMap) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            eventMap.put("eventId", incrementCounter("eventId"));
            graph.addVertex("class:Event", eventMap);
            graph.commit();
        } catch (Exception e) {
            graph.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
    }

    public static int incrementCounter(String name) {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            List list = graph.getRawGraph().command(new OCommandSQL("update Counter INCREMENT value = 1 return after where name = '" + name + "'")).execute();
            ODocument lastDoc = (ODocument)list.get(0);
            return lastDoc.field("value");
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
    }

    public static String getWhereClause(Map<String, Object> criteria) {
        String wc = null;
        boolean firstFilter = true;
        if(criteria.size() > 0) {
            // get the filter
            Iterator<String> it = criteria.keySet().iterator();
            StringBuilder sb = new StringBuilder();
            while(it.hasNext()) {
                String key = it.next();
                if(("pageNo").equals(key) || "pageSize".equals(key) || ("sortDir").equals(key) || "sortedBy".equals(key)) {
                    continue;
                } else {
                    Object filterValue = criteria.get(key);
                    if(filterValue != null && filterValue.toString().length() > 0) {
                        if(firstFilter) {
                            sb.append(" WHERE ");
                            firstFilter = false;
                        } else {
                            sb.append(" AND ");
                        }
                        if(filterValue instanceof String) {
                            // if key ends with DateFrom or DateTo then we need to treat it as Date
                            if(key.endsWith("DateFrom")) {
                                sb.append(key.substring(0, key.length() - 4));
                                sb.append(" >= date('").append(filterValue).append("')");
                            } else if(key.endsWith("DateTo")) {
                                sb.append(key.substring(0, key.length() - 2));
                                sb.append(" <= date('").append(filterValue).append("')");
                            } else  {
                                sb.append(key);
                                sb.append(" = '").append(filterValue).append("'");
                            }
                        } else {
                            sb.append(key);
                            sb.append(" = ").append(filterValue);
                        }
                    }
                    continue;
                }
            }
            wc = sb.toString();
        }
        return wc;
    }

    public static long getCount(String className, Map<String, Object> criteria) {
        long count = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) as count FROM ").append(className);
        String whereClause = DbService.getWhereClause(criteria);
        if(whereClause != null && whereClause.length() > 0) {
            sql.append(whereClause);
        }
        logger.debug("sql={}", sql);
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Iterable<Vertex> it =  graph.command(new OCommandSQL(sql.toString())).execute();
            if(it != null) {
                Vertex v = it.iterator().next();
                count = v.getProperty("count");
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return count;
    }

    public static String getData(String className, Map<String, Object> criteria) {
        String json = null;
        StringBuilder sql = new StringBuilder("SELECT FROM ").append(className);
        String whereClause = DbService.getWhereClause(criteria);
        if(whereClause != null && whereClause.length() > 0) {
            sql.append(whereClause);
        }
        String sortedBy = (String)criteria.get("sortedBy");
        String sortDir = (String)criteria.get("sortDir");
        if(sortedBy != null) {
            sql.append(" ORDER BY ").append(sortedBy);
            if(sortDir != null) {
                sql.append(" ").append(sortDir);
            }
        }
        Integer pageSize = (Integer)criteria.get("pageSize");
        Integer pageNo = (Integer)criteria.get("pageNo");
        if(pageNo != null && pageSize != null) {
            sql.append(" SKIP ").append((pageNo - 1) * pageSize);
            sql.append(" LIMIT ").append(pageSize);
        }
        logger.debug("sql={}", sql);
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            List<ODocument> list =  graph.getRawGraph().command(new OCommandSQL(sql.toString())).execute();
            if(list.size() > 0) {
                json = OJSONWriter.listToJSON(list, null);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }

    public static int executeSqlCommand(String sql) {
        int recordsUpdated = 0;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            recordsUpdated = graph.getRawGraph().command(new OCommandSQL(sql)).execute();
            graph.commit();
        } catch (Exception e) {
            graph.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return recordsUpdated;
    }

    public static Vertex getVertexByRid(OrientGraphNoTx graph, String rid) {
        return graph.getVertex(rid);
    }

    public static String getJsonByRid(String rid) {
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            ODocument doc = graph.getVertex(rid).getRecord();
            if(doc != null) json = doc.toJSON();
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }

    public static Vertex delVertexByRid(String rid) throws Exception {
        Vertex v = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            v = graph.getVertex(rid);
            graph.removeVertex(v);
            graph.commit();
        } catch (Exception e) {
            graph.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return v;
    }

    public static boolean hasEdgeToClass(OrientGraphNoTx graph, OrientVertex vertex, String edgeName) throws Exception {
        boolean result = false;
        if(vertex.countEdges(Direction.IN, edgeName) > 0) {
            result = true;
        }
        return result;
    }
}
