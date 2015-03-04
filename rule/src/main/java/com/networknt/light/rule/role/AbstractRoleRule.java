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

package com.networknt.light.rule.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/31/2014.
 */
public abstract class AbstractRoleRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractRoleRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    protected String getRoleById(String roleId) {
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex role = (OrientVertex)graph.getVertexByKey("Role.roleId", roleId);
            if(role != null) {
                json = role.getRecord().toJSON();
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }

    protected void addRole(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            OrientVertex role = graph.addVertex("class:Role", data);
            createUser.addEdge("Create", role);
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
    }

    protected void updRole(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex updateUser = graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            Vertex role = graph.getVertexByKey("Role.roleId", data.get("roleId"));
            if(role != null) {
                String host = (String)data.get("host");
                if(host != null && host.length() > 0) {
                    if(!host.equals(role.getProperty("host"))) role.setProperty("host", host);
                } else {
                    role.removeProperty("host");
                }
                String desc = (String)data.get("desc");
                if(desc != null && !desc.equals(role.getProperty("desc"))) {
                    role.setProperty("desc", desc);
                }
                role.setProperty("updateDate", data.get("updateDate"));
                updateUser.addEdge("Update", role);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
    }

    protected void delRole(String roleId) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex role = graph.getVertexByKey("Role.roleId", roleId);
            if(role != null) {
                graph.removeVertex(role);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
    }

    protected String getRoles(OrientGraph graph, String host) {
        String sql = "SELECT FROM Role";
        if(host != null) {
            sql = sql + " WHERE host = '" + host + "' OR host IS NULL";
        }
        String json = null;
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> roles = graph.getRawGraph().command(query).execute();
            json = OJSONWriter.listToJSON(roles, null);
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }

    protected String getRoleDropdown(String host) throws Exception {
        String sql = "SELECT FROM Role";
        if(host != null) {
            sql = sql + " WHERE host = '" + host + "' OR host IS NULL";
        }
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> roles = graph.getRawGraph().command(query).execute();
            if(roles.size() > 0) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for(ODocument doc: roles) {
                    Map<String, String> map = new HashMap<String, String>();
                    String roleId = doc.field("roleId");
                    map.put("label", roleId);
                    map.put("value", roleId);
                    list.add(map);
                }
                json = mapper.writeValueAsString(list);
            }

        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }
}
