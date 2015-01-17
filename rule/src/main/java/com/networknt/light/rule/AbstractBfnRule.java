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

package com.networknt.light.rule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.*;

/**
 * Created by steve on 28/12/14.
 * This the abstract class that implements functions for Blog, Forum and News
 */
public abstract class AbstractBfnRule  extends AbstractRule implements Rule {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    public abstract boolean execute (Object ...objects) throws Exception;

    public boolean addBfn (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String id = (String) data.get("id");
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            // TODO put this in config.
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains(bfnType + "Admin")) {
                error = "Role owner or admin or " + bfnType + "Admin is required to add " + bfnType;
                inputMap.put("responseCode", 403);
            } else {
                String userHost = (String)user.get("host");
                if(userHost != null && !userHost.equals(host)) {
                    error = "User can only add " + bfnType + " from host: " + host;
                    inputMap.put("responseCode", 403);
                } else {
                    ODocument doc = getODocumentByHostId(bfnType + "HostIdIdx", host, id);
                    if(doc == null) {
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.putAll((Map<String, Object>) inputMap.get("data"));
                        eventData.put("createDate", new java.util.Date());
                        eventData.put("createUserId", user.get("userId"));

                        // make sure parent exists if it is not empty.
                        String parentRid = (String)data.get("parent");
                        if(parentRid != null) {
                            ODocument parent = DbService.getODocumentByRid(parentRid);
                            if(parent == null) {
                                error = "Parent with @rid " + parentRid + " cannot be found.";
                                inputMap.put("responseCode", 404);
                            } else {
                                // convert parent from @rid to id
                                eventData.put("parent", parent.field("id"));
                            }
                        }
                        // make sure all children exist if there are any.
                        // and make sure all children have empty parent.
                        List<String> childrenRids = (List<String>)data.get("children");
                        if(childrenRids != null && childrenRids.size() > 0) {
                            List<String> childrenIds = new ArrayList<String>();
                            for(String childRid: childrenRids) {
                                if(childRid != null) {
                                    if(childRid.equals(parentRid)) {
                                        error = "Parent shows up in the Children list";
                                        inputMap.put("responseCode", 400);
                                        break;
                                    }
                                    ODocument child = DbService.getODocumentByRid(childRid);
                                    if(child == null) {
                                        error = "Child with @rid " + childRid + " cannot be found.";
                                        inputMap.put("responseCode", 404);
                                        break;
                                    } else {
                                        childrenIds.add(child.field("id"));
                                    }
                                }
                            }
                            eventData.put("children", childrenIds);
                        }
                    } else {
                        error = "Id " + id + " exists on host " + host;
                        inputMap.put("responseCode", 400);
                    }
                }
            }
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }

    public boolean addBfnEv (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addBfnDb(bfnType, data);
        return true;
    }

    protected ODocument addBfnDb(String bfnType, Map<String, Object> data) throws Exception {
        ODocument doc = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            OIndex<?> docHostIdIdx = db.getMetadata().getIndexManager().getIndex(bfnType + "HostIdIdx");
            String className = bfnType.substring(0, 1).toUpperCase() + bfnType.substring(1);
            doc = new ODocument(schema.getClass(className));
            doc.field("host", data.get("host"));
            doc.field("id", data.get("id"));
            if(data.get("desc") != null) doc.field("desc", data.get("desc"));
            if(data.get("attributes") != null) doc.field("attributes", data.get("attributes"));
            doc.field("createDate", data.get("createDate"));
            doc.field("createUserId", data.get("createUserId"));
            // parent
            if(data.get("parent") != null) {
                OCompositeKey parentKey = new OCompositeKey(data.get("host"), data.get("parent"));
                OIdentifiable parentOid = (OIdentifiable) docHostIdIdx.get(parentKey);
                if(parentOid != null) {
                    ODocument parent = (ODocument)parentOid.getRecord();
                    doc.field("parent", parent);
                    // update parent with the children
                    Set children = parent.field("children");
                    if(children != null) {
                        children.add(doc);
                    } else {
                        children = new HashSet<ODocument>();
                        children.add(doc);
                        parent.field("children", children);
                    }
                    parent.save();
                }
            }
            // children
            List<String> childrenIds = (List<String>)data.get("children");
            if(childrenIds != null) {
                Set children = new HashSet<ODocument>();
                for(String childId: childrenIds) {
                    OCompositeKey childKey = new OCompositeKey(data.get("host"), childId);
                    OIdentifiable childOid = (OIdentifiable) docHostIdIdx.get(childKey);
                    if(childOid != null) {
                        ODocument child = (ODocument)childOid.getRecord();
                        children.add(child);
                        child.field("parent", doc);
                        child.save();
                    }
                }
                doc.field("children", children);
            }
            doc.save();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return doc;
    }

    public boolean delBfn (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains(bfnType + "Admin")) {
                error = "Role owner or admin or " + bfnType + "Admin is required to delete " + bfnType;
                inputMap.put("responseCode", 403);
            } else {
                String userHost = (String)user.get("host");
                if(userHost != null && !userHost.equals(host)) {
                    error = "User can only delete " + bfnType + " from host: " + host;
                    inputMap.put("responseCode", 403);
                } else {
                    ODocument doc = DbService.getODocumentByRid(rid);
                    if(doc != null) {
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.put("host", doc.field("host"));
                        eventData.put("id", doc.field("id"));
                    } else {
                        error = "@rid " + rid + " doesn't exist on host " + host;
                        inputMap.put("responseCode", 400);
                    }
                }
            }
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }

    public boolean delBfnEv (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        return delBfnDb(bfnType, data);
    }

    protected boolean delBfnDb(String bfnType, Map<String, Object> data) throws Exception {
        boolean result = false;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> docHostIdIdx = db.getMetadata().getIndexManager().getIndex(bfnType + "HostIdIdx");
            OCompositeKey key = new OCompositeKey(data.get("host"), data.get("id"));
            OIdentifiable oid = (OIdentifiable) docHostIdIdx.get(key);
            if (oid != null) {
                ODocument doc = (ODocument) oid.getRecord();
                // update references from parent and children
                ODocument parent = doc.field("parent");
                if(parent != null) {
                    Set children = parent.field("children");
                    if(children != null && children.size() > 0) {
                        children.remove(doc);
                    }
                    parent.save();
                }
                Set<ODocument> children = doc.field("children");
                if(children != null && children.size() > 0) {
                    for(ODocument child: children) {
                        if(child != null) {
                            child.removeField("parent");
                            child.save();
                        }

                    }
                }
                doc.delete();
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

    public boolean updBfn (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            inputMap.put("error", "Login is required");
            inputMap.put("responseCode", 401);
            return false;
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains(bfnType + "Admin")) {
                inputMap.put("error", "Role owner or admin or " + bfnType + "Admin is required to update " + bfnType);
                inputMap.put("responseCode", 403);
                return false;
            } else {
                String userHost = (String)user.get("host");
                if(userHost != null && !userHost.equals(host)) {
                    inputMap.put("error", "User can only update " + bfnType + " from host: " + host);
                    inputMap.put("responseCode", 403);
                    return false;
                } else {
                    ODocument doc = null;
                    if(rid != null) {
                        doc = DbService.getODocumentByRid(rid);
                        if(doc != null) {
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.putAll((Map<String, Object>)inputMap.get("data"));
                            eventData.put("host", doc.field("host"));
                            eventData.put("id", doc.field("id"));
                            eventData.put("updateDate", new java.util.Date());
                            eventData.put("updateUserId", user.get("userId"));

                            // make sure parent exists if it is not empty.
                            String parentRid = (String)data.get("parent");
                            if(parentRid != null) {
                                if(rid.equals(parentRid)) {
                                    inputMap.put("error", "parent @rid is the same as current @rid");
                                    inputMap.put("responseCode", 400);
                                    return false;
                                }
                                ODocument parent = DbService.getODocumentByRid(parentRid);
                                if(parent == null) {
                                    inputMap.put("error", "Parent with @rid " + parentRid + " cannot be found");
                                    inputMap.put("responseCode", 404);
                                    return false;
                                } else {
                                    // convert parent from @rid to id
                                    eventData.put("parent", parent.field("id"));
                                }
                            }
                            // make sure all children exist if there are any.
                            // and make sure all children have empty parent.
                            List<String> childrenRids = (List<String>)data.get("children");
                            if(childrenRids != null && childrenRids.size() > 0) {
                                List<String> childrenIds = new ArrayList<String>();
                                for(String childRid: childrenRids) {
                                    if(childRid != null) {
                                        if(childRid.equals(parentRid)) {
                                            inputMap.put("error", "Parent shows up in the Children list");
                                            inputMap.put("responseCode", 400);
                                            return false;
                                        }
                                        if(childRid.equals(rid)) {
                                            inputMap.put("error", "Current object shows up in the Children list");
                                            inputMap.put("responseCode", 400);
                                            return false;
                                        }
                                        ODocument child = DbService.getODocumentByRid(childRid);
                                        if(child == null) {
                                            inputMap.put("error", "Child with @rid " + childRid + " cannot be found");
                                            inputMap.put("responseCode", 404);
                                            return false;
                                        } else {
                                            childrenIds.add(child.field("id"));
                                        }
                                    }
                                }
                                eventData.put("children", childrenIds);
                            }
                        } else {
                            inputMap.put("error", "@rid " + rid + " cannot be found");
                            inputMap.put("responseCode", 404);
                            return false;
                        }
                    } else {
                        inputMap.put("error", "@rid is required");
                        inputMap.put("responseCode", 400);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean updBfnEv (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updBfnDb(bfnType, data);
        return true;
    }

    protected ODocument updBfnDb(String bfnType, Map<String, Object> data) throws Exception {
        ODocument doc = null;
        // update parent according to children and update children according to parent.
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> docHostIdIdx = db.getMetadata().getIndexManager().getIndex(bfnType + "HostIdIdx");
            OCompositeKey key = new OCompositeKey(data.get("host"), data.get("id"));
            OIdentifiable oid = (OIdentifiable) docHostIdIdx.get(key);
            if (oid != null) {
                doc = (ODocument) oid.getRecord();
                if(data.get("desc") != null) {
                    doc.field("desc", data.get("desc"));
                } else {
                    doc.removeField("desc");
                }
                if(data.get("attributes") != null) {
                    doc.field("attributes", data.get("attributes"));
                } else {
                    doc.removeField("attributes");
                }
                if(data.get("parent") != null) {
                    OCompositeKey parentKey = new OCompositeKey(data.get("host"), data.get("parent"));
                    OIdentifiable parentOid = (OIdentifiable) docHostIdIdx.get(parentKey);
                    if(parentOid != null) {
                        ODocument parent = (ODocument)parentOid.getRecord();
                        doc.field("parent", parent);
                        // update parent with the children
                        Set children = parent.field("children");
                        if(children != null) {
                            children.add(doc);
                        } else {
                            children = new HashSet<ODocument>();
                            children.add(doc);
                            parent.field("children", children);
                        }
                        parent.save();
                    }
                } else {
                    ODocument parent = doc.field("parent");
                    if(parent != null) {
                        Set children = parent.field("children");
                        children.remove(doc);
                        doc.removeField("parent");
                    }
                }
                List<String> list = (List)data.get("children");
                if(list != null && list.size() > 0) {
                    Set<ODocument> storedChildren = doc.field("children");
                    if(storedChildren != null && storedChildren.size() > 0) {
                        // both lists are not empty. comparison is needed.
                        // first populate inputChildren as ODocument
                        Set<ODocument> inputChildren = new HashSet<ODocument>();
                        for(String id: list) {
                            OCompositeKey childKey = new OCompositeKey(data.get("host"), id);
                            OIdentifiable childOid = (OIdentifiable) docHostIdIdx.get(childKey);
                            if(childOid != null) inputChildren.add(childOid.getRecord());
                        }

                        Set<ODocument> addSet = new HashSet<ODocument>(inputChildren);
                        Set<ODocument> delSet = new HashSet<ODocument>(storedChildren);

                        addSet.removeAll(storedChildren);
                        if(addSet.size() > 0) {
                            for(ODocument addDoc: addSet) {
                                addDoc.field("parent", doc);
                                storedChildren.add(addDoc);
                                addDoc.save();
                                doc.save();
                            }
                        }
                        delSet.removeAll(inputChildren);
                        if(delSet.size() > 0) {
                            for(ODocument delDoc: delSet) {
                                delDoc.removeField("parent");
                                storedChildren.remove(delDoc);
                                delDoc.save();
                                doc.save();
                            }
                        }
                    } else {
                        // doesn't have children
                        storedChildren = new HashSet<ODocument>();
                        for(String id: (List<String>)data.get("children")) {
                            OCompositeKey childKey = new OCompositeKey(data.get("host"), id);
                            OIdentifiable childOid = (OIdentifiable) docHostIdIdx.get(childKey);
                            if(childOid != null) {
                                ODocument child = childOid.getRecord();
                                if(child != null) {
                                    storedChildren.add(childOid.getRecord());
                                    child.field("parent", doc);
                                    child.save();
                                }
                            }
                        }
                        doc.field("children", storedChildren);
                    }
                } else {
                    Set<ODocument> children = doc.field("children");
                    if(children != null && children.size() > 0) {
                        for(ODocument child: children) {
                            child.removeField("parent");
                        }
                    }
                    doc.removeField("children");
                }
                doc.field("updateDate", data.get("updateDate"));
                doc.field("updateUserId", data.get("updateUserId"));
                doc.save();
                db.commit();
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return doc;
    }


    public boolean addPost(String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String parentId = (String) data.get("parentId");
        String host = (String) data.get("host");
        String title = (String) data.get("title");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            if(parentId == null || host == null || title == null) {
                error = "ParentId, Host and Title are required";
                inputMap.put("responseCode", 400);
            } else {
                //  make sure parent exists.
                ODocument doc = getODocumentByHostId(bfnType + "HostIdIdx", host, parentId);
                if(doc == null) {
                    error = "Id " + parentId + " doesn't exist on host " + host;
                    inputMap.put("responseCode", 400);
                } else {
                    Map<String, Object> user = (Map<String, Object>)payload.get("user");
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.putAll((Map<String, Object>) inputMap.get("data"));
                    eventData.put("id", DbService.incrementCounter("postId"));
                    eventData.put("createDate", new java.util.Date());
                    eventData.put("createUserId", user.get("userId"));
                }
            }
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }

    public boolean addPostEv (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addPostDb(bfnType, data);
        return true;
    }

    protected ODocument addPostDb(String bfnType, Map<String, Object> data) throws Exception {
        ODocument post = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            post = new ODocument(schema.getClass("Post"));
            post.field("host", data.get("host"));
            post.field("id", data.get("id"));
            post.field("title", data.get("title"));
            if(data.get("summary") != null) post.field("summary", data.get("summary"));
            if(data.get("content") != null) post.field("content", data.get("content"));
            post.field("createDate", data.get("createDate"));
            post.field("createUserId", data.get("createUserId"));
            // parent
            OIndex<?> docHostIdIdx = db.getMetadata().getIndexManager().getIndex(bfnType + "HostIdIdx");
            OCompositeKey parentKey = new OCompositeKey(data.get("host"), data.get("parentId"));
            OIdentifiable parentOid = (OIdentifiable) docHostIdIdx.get(parentKey);
            if(parentOid != null) {
                ODocument parent = (ODocument)parentOid.getRecord();
                post.field("parent", parent);
                // update parent with the posts
                List posts = parent.field("posts");
                if(posts != null) {
                    posts.add(post);
                } else {
                    posts = new ArrayList<ODocument>();
                    posts.add(post);
                    parent.field("posts", posts);
                }
                parent.save();
            }
            // tags
            Map<String, Object> tagMap = new HashMap<String, Object>();
            Set<String> inputTags = data.get("tags") != null? new HashSet<String>(Arrays.asList(((String)data.get("tags")).split("\\s*,\\s*"))) : new HashSet<String>();
            String host = (String)data.get("host");
            String className = post.getClassName();
            for(String tagName: inputTags) {
                ODocument tag = null;
                // get the tag is it exists
                OIndex<?> hostNameClassIdx = db.getMetadata().getIndexManager().getIndex("hostNameClassIdx");
                OCompositeKey tagKey = new OCompositeKey(host, tagName, className);
                OIdentifiable tagOid = (OIdentifiable) hostNameClassIdx.get(tagKey);
                if (tagOid != null) {
                    tag = (ODocument) tagOid.getRecord();
                    Set links = tag.field("links");
                    links.add(post);
                    tag.save();
                } else {
                    tag = new ODocument(schema.getClass("Tag"));
                    tag.field("host", host);
                    tag.field("name", tagName);
                    tag.field("class", className);
                    tag.field("createDate", data.get("createDate"));
                    tag.field("createUserId", data.get("createUserId"));
                    Set links = new HashSet<String>();
                    links.add(post);
                    tag.field("links", links);
                    tag.save();
                }
                tagMap.put(tagName, tag);
            }
            post.field("tags", tagMap);

            post.save();
            // synch post id
            OIndex<?> counterNameIdx = db.getMetadata().getIndexManager().getIndex("Counter.name");
            OIdentifiable counterOid = (OIdentifiable) counterNameIdx.get("postId");
            if (counterOid != null) {
                ODocument counter = (ODocument) counterOid.getRecord();
                if(!data.get("id").equals(counter.field("value"))) {
                    counter.field("value", data.get("id"));
                    counter.save();
                }
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return post;
    }

    public boolean getBfnTree(String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
        String json = getBfnTree(bfnType, host);
        if(json != null) {
            inputMap.put("result", json);
            return true;
        } else {
            inputMap.put("error", "No document can be found");
            inputMap.put("responseCode", 404);
            return false;
        }
    }

    protected String getBfnTree(String bfnType, String host) {
        String json = null;
        String sql = "SELECT FROM " + bfnType + " WHERE host = ? and parent IS NULL ORDER BY id";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> docs = db.command(query).execute(host);
            if(docs.size() > 0) {
                json = OJSONWriter.listToJSON(docs, "fetchPlan:*:-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return json;
    }

    public boolean getBfnPost(String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        if(data.get("id") == null || data.get("host") == null) {
            inputMap.put("error", "Host and Id are required");
            inputMap.put("responseCode", 400);
            return false;
        }
        String posts = getBfnPostDb(bfnType, data);
        if(posts != null) {
            inputMap.put("result", posts);
            return true;
        } else {
            inputMap.put("error", "No post can be found");
            inputMap.put("responseCode", 404);
            return false;
        }
    }

    protected String getBfnPostDb(String bfnType, Map<String, Object> data) {
        String json = null;
        String sql = "select from (traverse posts, children from (select from " + bfnType + " where host = ? and id = ?)) where @class = 'Post'";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> forums = db.command(query).execute(data.get("host"), data.get("id"));
            if(forums.size() > 0) {
                json = OJSONWriter.listToJSON(forums, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return json;
    }

    public boolean getBfn(String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String host = (String)data.get("host");
        if(payload == null) {
            inputMap.put("error", "Login is required");
            inputMap.put("responseCode", 401);
            return false;
        } else {
            Map<String, Object> user = (Map<String, Object>) payload.get("user");
            List roles = (List)user.get("roles");
            if(roles.contains("owner") || roles.contains("admin") || roles.contains(bfnType + "Admin")) {
                Object userHost = user.get("host");
                if(userHost != null && !userHost.equals(host)) {
                    inputMap.put("error", "User can only get " + bfnType + " from host: " + host);
                    inputMap.put("responseCode", 403);
                    return false;
                } else {
                    String docs = getBfnDb(bfnType, host);
                    if(docs != null) {
                        inputMap.put("result", docs);
                        return true;
                    } else {
                        inputMap.put("error", "No document can be found");
                        inputMap.put("responseCode", 404);
                        return false;
                    }
                }
            } else {
                inputMap.put("error", "Role owner or admin or forumAdmin is required to get all forums");
                inputMap.put("responseCode", 401);
                return false;
            }
        }
    }

    protected String getBfnDb(String bfhType, String host) {
        String json = null;
        String sql = "SELECT FROM " + bfhType + " WHERE host = ? ORDER BY createDate";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> docs = db.command(query).execute(host);
            if(docs.size() > 0) {
                json = OJSONWriter.listToJSON(docs, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return json;
    }

    public boolean getBfnDropdown (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String host = (String)data.get("host");
        if(payload == null) {
            inputMap.put("error", "Login is required");
            inputMap.put("responseCode", 401);
            return false;
        } else {
            String docs = getBfnDropdownDb(bfnType, host);
            if(docs != null) {
                inputMap.put("result", docs);
                return true;
            } else {
                inputMap.put("error", "No document can be found");
                inputMap.put("responseCode", 404);
                return false;
            }
        }
    }

    protected String getBfnDropdownDb(String bfnType, String host) {
        String json = null;
        String sql = "SELECT FROM " + bfnType + " WHERE host = ? ORDER BY id";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> docs = db.command(query).execute(host);
            if(docs.size() > 0) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for(ODocument doc: docs) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("id", doc.field("id"));
                    map.put("value", doc.field("@rid").toString());
                    list.add(map);
                }
                json = mapper.writeValueAsString(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return json;
    }

}
