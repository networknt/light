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
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> emailIdx = db.getMetadata().getIndexManager().getIndex("User.email");
            // this is a unique index, so it retrieves a OIdentifiable
            userInDb = emailIdx.contains(email);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return userInDb;
    }

    protected boolean isUserInDbByUserId(String userId) {
        boolean userInDb = false;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
            // this is a unique index, so it retrieves a OIdentifiable
            userInDb = userIdIdx.contains(userId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return userInDb;
    }

    protected ODocument getUserByUserId(String userId) {
        ODocument user = null;
        StringBuilder sb = new StringBuilder("SELECT FROM User WHERE userId = '");
        sb.append(userId).append("'");
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sb.toString());
            List<ODocument> list = db.command(query.setFetchPlan("*:-1")).execute();
            if(list.size() > 0) {
                user = list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return user;
    }

    protected ODocument getUserByEmail(String email) {
        ODocument user = null;
        StringBuilder sb = new StringBuilder("SELECT FROM User WHERE email = '");
        sb.append(email).append("' FETCHPLAN credential:1");
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sb.toString());
            List<ODocument> list = db.command(query).execute();
            if(list.size() > 0) {
                user = list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return user;
    }

    protected ODocument addUser(Map<String, Object> data) throws Exception {
        ODocument user = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OSchema schema = db.getMetadata().getSchema();
            ODocument credential = new ODocument(schema.getClass("Credential"));
            credential.field("password", data.get("password"));
            credential.save();
            user = new ODocument(schema.getClass("User"));
            user.field("host", data.get("host"));
            user.field("userId", data.get("userId"));
            user.field("email", data.get("email"));
            user.field("firstName", data.get("firstName"));
            user.field("lastName", data.get("lastName"));
            user.field("karma", 0);
            List<String> roles = new ArrayList<String>();
            roles.add("user"); // default role for sign up users, more roles can be added later by admin
            user.field("roles", roles);
            user.field("credential", credential);
            user.field("createDate", new java.util.Date());
            user.save();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return user;
    }

    protected boolean delUser(Map<String, Object> data) throws Exception {
        boolean result = false;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get("userId"));
            if (oid != null) {
                ODocument user = (ODocument) oid.getRecord();
                ODocument credential = (ODocument)user.field("credential");
                credential.delete();
                user.delete();
                db.commit();
                result = true;
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return result;
    }

    protected ODocument updPassword(Map<String, Object> data) throws Exception {
        ODocument credential = null;
        ODocument user = getUserByUserId((String)data.get("userId"));
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            user.field("updateDate", data.get("updateDate"));
            user.save();
            credential = user.field("credential");
            if (credential != null) {
                credential.field("password", data.get("password"));
                credential.save();
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return credential;
    }

    protected ODocument updRole(Map<String, Object> data) throws Exception {
        ODocument user = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get("userId"));
            if (oid != null) {
                user = (ODocument) oid.getRecord();
                user.field("roles", data.get("roles"));
                user.field("updateDate", data.get("updateDate"));
                user.field("updateUserId", data.get("updateUserId"));
                user.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return user;
    }

    protected ODocument addRole(Map<String, Object> data) throws Exception {
        ODocument user = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get("userId"));
            if (oid != null) {
                user = (ODocument) oid.getRecord();
                List roles = user.field("roles");
                roles.add((String)data.get("role"));
                user.field("updateDate", data.get("updateDate"));
                user.field("updateUserId", data.get("updateUserId"));
                user.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return user;
    }

    protected ODocument delRole(Map<String, Object> data) throws Exception {
        ODocument user = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get("userId"));
            if (oid != null) {
                user = (ODocument) oid.getRecord();
                List roles = user.field("roles");
                roles.remove((String)data.get("role"));
                user.field("updateDate", data.get("updateDate"));
                user.field("updateUserId", data.get("updateUserId"));
                user.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return user;
    }

    protected ODocument updLockByUserId(Map<String, Object> data) throws Exception {
        ODocument user = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get("userId"));
            if (oid != null) {
                user = (ODocument) oid.getRecord();
                user.field("locked", data.get("locked"));
                user.field("updateDate", data.get("updateDate"));
                user.field("updateUserId", data.get("updateUserId"));
                user.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return user;
    }

    protected ODocument updUser(Map<String, Object> data) throws Exception {
        ODocument user = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get("userId"));
            if (oid != null) {
                user = (ODocument) oid.getRecord();
                String firstName = (String)data.get("firstName");
                if(firstName != null && !firstName.equals(user.field("firstName"))) {
                    user.field("firstName", firstName);
                }
                String lastName = (String)data.get("lastName");
                if(lastName != null && !lastName.equals(user.field("lastName"))) {
                    user.field("lastName", lastName);
                }
                user.field("updateDate", data.get("updateDate"));
                user.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return user;
    }

    protected void revokeRefreshToken(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
            OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get("userId"));
            if (oid != null) {
                ODocument user = (ODocument) oid.getRecord();
                ODocument credential = (ODocument)user.field("credential");
                if(credential != null) {
                    // remove hostRefreshTokens map here. That means all the refresh token will be
                    // removed for the user even the token is for other hosts.
                    credential.removeField("hostRefreshTokens");
                    credential.save();
                    db.commit();
                }
            }
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
    }


    protected void signIn(Map<String, Object> data) throws Exception {
        String hashedRefreshToken = (String)data.get("hashedRefreshToken");
        if(hashedRefreshToken != null) {
            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
            try {
                db.begin();
                OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
                OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get("userId"));
                if (oid != null) {
                    ODocument user = (ODocument) oid.getRecord();
                    ODocument credential = (ODocument)user.field("credential");
                    if(credential != null) {
                        String host = (String)data.get("host");
                        // get hostRefreshTokens map here.
                        Map hostRefreshTokens = credential.field("hostRefreshTokens");
                        if(hostRefreshTokens != null) {
                            // logged in before, check if logged in from the host.
                            List<String> refreshTokens = (List)hostRefreshTokens.get(host);
                            if(refreshTokens != null) {
                                // max refresh tokens for user is 10. max 10 devices.
                                if(refreshTokens.size() >= 10) {
                                    refreshTokens.remove(0);
                                }
                                refreshTokens.add(hashedRefreshToken);
                            } else {
                                refreshTokens = new ArrayList<String>();
                                refreshTokens.add(hashedRefreshToken);
                                hostRefreshTokens.put(host, refreshTokens);
                            }

                        } else {
                            // never logged in, create the map.
                            hostRefreshTokens = new HashMap<String, List<String>>();
                            List<String> refreshTokens = new ArrayList<String>();
                            refreshTokens.add(hashedRefreshToken);
                            hostRefreshTokens.put(host, refreshTokens);
                            credential.field("hostRefreshTokens", hostRefreshTokens);
                        }
                        credential.save();
                        db.commit();
                    }
                }
            } catch (Exception e) {
                db.rollback();
                logger.error("Exception:", e);
                throw e;
            } finally {
                db.close();
            }
        } else {
            logger.debug("There is no hashedRefreshToken as user didn't select remember me. Do nothing");
        }
    }

    protected void logOut(Map<String, Object> data) throws Exception {
        String refreshToken = (String)data.get("refreshToken");
        if(refreshToken != null) {
            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
            try {
                db.begin();
                OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
                OIdentifiable oid = (OIdentifiable) userIdIdx.get((String)data.get("userId"));
                if (oid != null) {
                    ODocument user = (ODocument) oid.getRecord();
                    ODocument credential = (ODocument)user.field("credential");
                    if(credential != null) {
                        // now remove the refresh token
                        String host = (String)data.get("host");
                        logger.debug("logOut to remove refreshToken {} from host {}" , refreshToken, host);
                        Map hostRefreshTokens = credential.field("hostRefreshTokens");
                        if(hostRefreshTokens != null) {
                            // logged in before, check if logged in from the host.
                            List<String> refreshTokens = (List)hostRefreshTokens.get(host);
                            if(refreshTokens != null) {
                                String hashedRefreshToken = HashUtil.md5(refreshToken);
                                refreshTokens.remove(hashedRefreshToken);
                            }
                        } else {
                            logger.error("There is no refresh tokens");
                        }
                        credential.save();
                        db.commit();
                    }
                }
            } catch (Exception e) {
                db.rollback();
                e.printStackTrace();
                throw e;
            } finally {
                db.close();
            }
        } else {
            logger.debug("There is no hashedRefreshToken as user didn't pass in refresh token when logging out. Do nothing");
        }
    }

    boolean checkRefreshToken(ODocument credential, String host, String refreshToken) throws Exception {
        boolean result = false;
        if(credential != null && refreshToken != null) {
            Map hostRefreshTokens = credential.field("hostRefreshTokens");
            if(hostRefreshTokens != null) {
                List<String> refreshTokens = (List)hostRefreshTokens.get(host);
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
        ODocument user = null;
        ODocument voteUser = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
            OIdentifiable userOid = (OIdentifiable) userIdIdx.get((String)data.get("userId"));
            OIdentifiable voteUserOid = (OIdentifiable)userIdIdx.get((String)data.get("voteUserId"));
            if (userOid != null && voteUserOid != null) {
                user = (ODocument) userOid.getRecord();
                voteUser = (ODocument)voteUserOid.getRecord();
                Set upSet = user.field("upUsers");
                if(upSet == null) {
                    upSet = new HashSet<String>();
                    upSet.add(voteUser);
                    user.field("upUsers", upSet);
                } else {
                    upSet.add(voteUser);
                }
                Set downSet = user.field("downUsers");
                if(downSet != null) {
                    downSet.remove(voteUser);
                }
                user.field("updateDate", data.get("updateDate"));
                user.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
    }

    protected void downVoteUser(Map<String, Object> data) {
        ODocument user = null;
        ODocument voteUser = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
            OIdentifiable userOid = (OIdentifiable) userIdIdx.get((String)data.get("userId"));
            OIdentifiable voteUserOid = (OIdentifiable)userIdIdx.get((String)data.get("voteUserId"));
            if (userOid != null && voteUserOid != null) {
                user = (ODocument) userOid.getRecord();
                voteUser = (ODocument)voteUserOid.getRecord();
                Set downSet = user.field("downUsers");
                if(downSet == null) {
                    downSet = new HashSet<String>();
                    downSet.add(voteUser);
                    user.field("downUsers", downSet);
                } else {
                    downSet.add(voteUser);
                }
                Set upSet = user.field("upUsers");
                if(upSet != null) {
                    upSet.remove(voteUser);
                }
                user.field("updateDate", data.get("updateDate"));
                user.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
    }

    // TODO refactor it to be generic. table name as part of the criteria? or a parameter?
    protected long getTotalNumberUserFromDb(Map<String, Object> criteria) {
        long total = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) as count FROM User");

        String whereClause = DbService.getWhereClause(criteria);
        if(whereClause != null && whereClause.length() > 0) {
            sql.append(whereClause);
        }

        System.out.println("sql=" + sql);
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            total = ((ODocument)db.query(new OSQLSynchQuery<ODocument>(sql.toString())).get(0)).field("count");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return total;
    }

    protected List<String> getRoles() {
        List<String> roles = null;
        String sql = "SELECT id FROM Role";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> list = db.command(query).execute();
            if(list.size() > 0) {
                roles = new ArrayList<String>();
                for(ODocument doc: list) {
                    roles.add((String)doc.field("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return roles;
    }

    protected String getUserFromDb(Map<String, Object> criteria) {
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
        System.out.println("sql=" + sql);
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql.toString());
            List<ODocument> list = db.command(query).execute();
            if(list.size() > 0) {
                json = OJSONWriter.listToJSON(list, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return json;
    }

    boolean isEmail(String userIdEmail) {
        Matcher matcher = pattern.matcher(userIdEmail);
        return matcher.matches();
    }

    String generateToken(ODocument user) throws Exception {
        Map<String, Object> jwtMap = new LinkedHashMap<String, Object>();
        jwtMap.put("@rid", user.field("@rid").toString());
        jwtMap.put("userId", user.field("userId"));
        if(user.field("host") != null) {
            jwtMap.put("host", user.field("host"));
        }
        jwtMap.put("roles", user.field("roles"));
        return JwtUtil.getJwt(jwtMap);
    }

    boolean checkPassword(ODocument user, String inputPassword) throws Exception {
        ODocument credential = (ODocument)user.field("credential");
        String storedPassword = (String) credential.field("password");
        return HashUtil.validatePassword(inputPassword, storedPassword);
    }

}
