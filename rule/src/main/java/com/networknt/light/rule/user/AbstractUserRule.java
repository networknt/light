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

package com.networknt.light.rule.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.JwtUtil;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by steve on 9/23/2014.
 */
public abstract class AbstractUserRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractUserRule.class);

    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public abstract boolean execute (Object ...objects) throws Exception;
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    protected boolean isUserInDbByEmail(String email) {
        boolean userInDb = false;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Vertex user = graph.getVertexByKey("User.email", email);
            if(user != null) {
                userInDb = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            graph.shutdown();
        }
        return userInDb;
    }

    protected boolean isUserInDbByUserId(String userId) {
        boolean userInDb = false;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Vertex user = graph.getVertexByKey("User.userId", userId);
            if(user != null) {
                userInDb = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            graph.shutdown();
        }
        return userInDb;
    }

    protected Vertex getUserByUserId(OrientGraph graph, String userId) throws Exception {
        return graph.getVertexByKey("User.userId", userId);
    }

    protected Vertex getUserByEmail(OrientGraph graph, String email) throws Exception {
        return graph.getVertexByKey("User.email", email);
    }

    protected Vertex getCredential(OrientGraph graph, Vertex user) throws Exception {
        return graph.getVertex(user.getProperty("credential"));
    }

    protected Vertex addUser(Map<String, Object> data) throws Exception {
        Vertex user = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            String password = (String)data.remove("password");
            OrientVertex credential = graph.addVertex("class:Credential", "password", password);
            data.put("credential", credential);
            // calculate gravatar md5
            data.put("gravatar", HashUtil.md5Hex((String)data.get("email")));
            user = graph.addVertex("class:User", data);
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
        return user;
    }

    protected Vertex addActivation(String userId) throws Exception {
        Vertex activation = null;
        String code = HashUtil.generateUUID();
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            activation = graph.addVertex("class:Activation", "userId", userId, "code", code, "createDate", new Date());
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
        return activation;
    }

    protected String getActivationCode(String userId) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        String code = null;
        try {
            Vertex activation = graph.getVertexByKey("Activation.userId", userId);
            if(activation != null) {
                code = activation.getProperty("code");
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
        return code;
    }

    protected void delActivation(String userId, String code) throws Exception {
        Vertex activation = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            activation = graph.getVertexByKey("Activation.userId", userId);
            if(activation != null && code != null && code.equals(activation.getProperty("code"))) {
                activation.remove();
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

    protected void delUser(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex user = graph.getVertexByKey("User.userId", data.get("userId"));
            if(user != null) {
                graph.removeVertex(user.getProperty("credential"));
                graph.removeVertex(user);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
    }

    protected void updPassword(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex user = graph.getVertexByKey("User.userId", data.get("userId"));
            user.setProperty("updateDate", data.get("updateDate"));
            Vertex credential = user.getProperty("credential");
            if (credential != null) {
                credential.setProperty("password", data.get("password"));
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

    protected void updRole(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex user = graph.getVertexByKey("User.userId", data.get("userId"));
            if(user != null) {
                user.setProperty("roles", data.get("roles"));
                user.setProperty("updateDate", data.get("updateDate"));
                Vertex updateUser = graph.getVertexByKey("User.userId", data.get("updateUserId"));
                updateUser.addEdge("Update", user);
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

    protected void updLockByUserId(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex user = graph.getVertexByKey("User.userId", data.get("userId"));
            if(user != null) {
                user.setProperty("locked", data.get("locked"));
                user.setProperty("updateDate", data.get("updateDate"));
                Vertex updateUser = graph.getVertexByKey("User.userId", data.get("updateUserId"));
                updateUser.addEdge("Update", user);
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

    protected void updUser(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex user =  graph.getVertexByKey("User.userId", data.get("userId"));
            if(user != null) {
                String firstName = (String)data.get("firstName");
                if(firstName != null && !firstName.equals(user.getProperty("firstName"))) {
                    user.setProperty("firstName", firstName);
                }
                String lastName = (String)data.get("lastName");
                if(lastName != null && !lastName.equals(user.getProperty("lastName"))) {
                    user.setProperty("lastName", lastName);
                }
                // TODO update shipping address and payment address here.
                Map<String, Object> shippingAddress = (Map<String, Object>)data.get("shippingAddress");
                if(shippingAddress != null) {
                    user.setProperty("shippingAddress", shippingAddress);
                }
                Map<String, Object> paymentAddress = (Map<String, Object>)data.get("paymentAddress");
                if(paymentAddress != null) {
                    user.setProperty("paymentAddress", paymentAddress);
                }
                user.setProperty("updateDate", data.get("updateDate"));
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

    // TODO need to know which clientId to remove only for that client or all?
    protected void revokeRefreshToken(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex user = graph.getVertexByKey("User.userId", data.get("userId"));
            if(user != null) {
                Vertex credential = user.getProperty("credential");
                if(credential != null) {
                    credential.removeProperty("clientRefreshTokens");
                }
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


    protected void signIn(Map<String, Object> data) throws Exception {
        String hashedRefreshToken = (String)data.get("hashedRefreshToken");
        if(hashedRefreshToken != null) {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                graph.begin();
                Vertex user = graph.getVertexByKey("User.userId", data.get("userId"));
                if(user != null) {
                    Vertex credential = user.getProperty("credential");
                    if(credential != null) {
                        String clientId = (String)data.get("clientId");
                        // get hostRefreshTokens map here.
                        Map clientRefreshTokens = credential.getProperty("clientRefreshTokens");
                        if(clientRefreshTokens != null) {
                            // logged in before, check if logged in from the host.
                            List<String> refreshTokens = (List)clientRefreshTokens.get(clientId);
                            if(refreshTokens != null) {
                                // max refresh tokens for user is 10. max 10 devices.
                                if(refreshTokens.size() >= 10) {
                                    refreshTokens.remove(0);
                                }
                                refreshTokens.add(hashedRefreshToken);
                            } else {
                                refreshTokens = new ArrayList<String>();
                                refreshTokens.add(hashedRefreshToken);
                                clientRefreshTokens.put(clientId, refreshTokens);
                            }
                            credential.setProperty("clientRefreshTokens", clientRefreshTokens);
                        } else {
                            // never logged in, create the map.
                            clientRefreshTokens = new HashMap<String, List<String>>();
                            List<String> refreshTokens = new ArrayList<String>();
                            refreshTokens.add(hashedRefreshToken);
                            clientRefreshTokens.put(clientId, refreshTokens);
                            credential.setProperty("clientRefreshTokens", clientRefreshTokens);
                        }
                    }
                }
                graph.commit();
            } catch (Exception e) {
                logger.error("Exception:", e);
                graph.rollback();
                throw e;
            } finally {
                graph.shutdown();
            }
        } else {
            logger.debug("There is no hashedRefreshToken as user didn't select remember me. Do nothing");
        }
    }

    protected void logOut(Map<String, Object> data) throws Exception {
        String refreshToken = (String)data.get("refreshToken");
        if(refreshToken != null) {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                graph.begin();
                Vertex user = graph.getVertexByKey("User.userId", data.get("userId"));
                if(user != null) {
                    Vertex credential = user.getProperty("credential");
                    if(credential != null) {
                        // now remove the refresh token
                        String clientId = (String)data.get("clientId");
                        logger.debug("logOut to remove refreshToken {} from clientId {}" , refreshToken, clientId);
                        Map clientRefreshTokens = credential.getProperty("clientRefreshTokens");
                        if(clientRefreshTokens != null) {
                            // logged in before, check if logged in from the host.
                            List<String> refreshTokens = (List)clientRefreshTokens.get(clientId);
                            if(refreshTokens != null) {
                                String hashedRefreshToken = HashUtil.md5(refreshToken);
                                refreshTokens.remove(hashedRefreshToken);
                            }
                        } else {
                            logger.error("There is no refresh tokens");
                        }
                    }
                }
                graph.commit();
            } catch (Exception e) {
                logger.error("Exception:", e);
                graph.rollback();
                throw e;
            } finally {
                graph.shutdown();
            }
        } else {
            logger.debug("There is no hashedRefreshToken as user didn't pass in refresh token when logging out. Do nothing");
        }
    }

    boolean checkRefreshToken(Vertex credential, String clientId, String refreshToken) throws Exception {
        boolean result = false;
        if(credential != null && refreshToken != null) {
            Map clientRefreshTokens = credential.getProperty("clientRefreshTokens");
            if(clientRefreshTokens != null) {
                List<String> refreshTokens = (List)clientRefreshTokens.get(clientId);
                if(refreshTokens != null) {
                    String hashedRefreshToken = HashUtil.md5(refreshToken);
                    for(String token: refreshTokens) {
                        if(hashedRefreshToken.equals(token)) {
                            result = true;
                            break;
                        }
                    }
                }
            } else {
                logger.error("There is no refresh tokens");
            }
        }
        return result;
    }

    protected void upVoteUser(Map<String, Object> data) {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            OrientVertex user = (OrientVertex)graph.getVertexByKey("User.userId", data.get("userId"));
            OrientVertex voteUser = (OrientVertex)graph.getVertexByKey("User.userId", data.get("voteUserId"));
            if(user != null && voteUser != null) {
                for (Edge edge : voteUser.getEdges(user, Direction.OUT, "DownVote")) {
                    if(edge.getVertex(Direction.IN).equals(user)) graph.removeEdge(edge);
                }
                voteUser.addEdge("UpVote", user);
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

    protected void downVoteUser(Map<String, Object> data) {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            OrientVertex user = (OrientVertex)graph.getVertexByKey("User.userId", data.get("userId"));
            OrientVertex voteUser = (OrientVertex)graph.getVertexByKey("User.userId", data.get("voteUserId"));
            if(user != null && voteUser != null) {
                for (Edge edge : voteUser.getEdges(user, Direction.OUT, "UpVote")) {
                    if(edge.getVertex(Direction.IN).equals(user)) graph.removeEdge(edge);
                }
                voteUser.addEdge("DownVote", user);
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

    // TODO refactor it to be generic. table name as part of the criteria? or a parameter?
    protected long getTotalNumberUserFromDb(OrientGraph graph, Map<String, Object> criteria) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) as count FROM User");
        String whereClause = DbService.getWhereClause(criteria);
        if(whereClause != null && whereClause.length() > 0) {
            sql.append(whereClause);
        }
        logger.debug("sql=" + sql);
        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql.toString());
        List<ODocument> list = graph.getRawGraph().command(query).execute();
        return list.get(0).field("count");
    }

    protected String getUserFromDb(OrientGraph graph, Map<String, Object> criteria) throws Exception {
        String json = null;
        StringBuilder sql = new StringBuilder("SELECT FROM User ");
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
        logger.debug("sql=" + sql);
        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql.toString());
        List<ODocument> list = graph.getRawGraph().command(query).execute();
        if(list.size() > 0) {
            json = OJSONWriter.listToJSON(list, null);
        }
        return json;
    }

    boolean isEmail(String userIdEmail) {
        Matcher matcher = pattern.matcher(userIdEmail);
        return matcher.matches();
    }

    String generateToken(Vertex user, String clientId, Boolean rememberMe) throws Exception {
        Map<String, Object> jwtMap = new LinkedHashMap<String, Object>();
        jwtMap.put("@rid", user.getId().toString());
        jwtMap.put("userId", user.getProperty("userId"));
        jwtMap.put("clientId", clientId);
        jwtMap.put("roles", user.getProperty("roles"));
        return JwtUtil.getJwt(jwtMap, rememberMe);
    }

    boolean checkPassword(OrientGraph graph, Vertex user, String inputPassword) throws Exception {
        Vertex credential = user.getProperty("credential");
        //Vertex credential = getCredential(graph, user);
        String storedPassword = (String) credential.getProperty("password");
        return HashUtil.validatePassword(inputPassword, storedPassword);
    }

}
