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

package com.networknt.light.rule.forum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
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
 * Created by steve on 26/11/14.
 */
public abstract class AbstractForumRule extends AbstractRule implements Rule {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    public abstract boolean execute (Object ...objects) throws Exception;

    protected ODocument addForum(Map<String, Object> data) throws Exception {
        ODocument forum = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            OIndex<?> forumHostIdIdx = db.getMetadata().getIndexManager().getIndex("forumHostIdIdx");
            forum = new ODocument(schema.getClass("Forum"));
            forum.field("host", data.get("host"));
            forum.field("id", data.get("id"));
            if(data.get("desc") != null) forum.field("desc", data.get("desc"));
            if(data.get("attributes") != null) forum.field("attributes", data.get("attributes"));
            forum.field("createDate", data.get("createDate"));
            forum.field("createUserId", data.get("createUserId"));
            // parent
            if(data.get("parent") != null) {
                OCompositeKey parentKey = new OCompositeKey(data.get("host"), data.get("parent"));
                OIdentifiable parentOid = (OIdentifiable) forumHostIdIdx.get(parentKey);
                if(parentOid != null) {
                    ODocument parent = (ODocument)parentOid.getRecord();
                    forum.field("parent", parent);
                    // update parent with the children
                    Set children = parent.field("children");
                    if(children != null) {
                        children.add(forum);
                    } else {
                        children = new HashSet<ODocument>();
                        children.add(forum);
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
                    OIdentifiable childOid = (OIdentifiable) forumHostIdIdx.get(childKey);
                    if(childOid != null) {
                        ODocument child = (ODocument)childOid.getRecord();
                        children.add(child);
                        child.field("parent", forum);
                        child.save();
                    }
                }
                forum.field("children", children);
            }
            forum.save();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return forum;
    }

    protected boolean delForum(Map<String, Object> data) throws Exception {
        boolean result = false;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> forumHostIdIdx = db.getMetadata().getIndexManager().getIndex("forumHostIdIdx");
            OCompositeKey key = new OCompositeKey(data.get("host"), data.get("id"));
            OIdentifiable oid = (OIdentifiable) forumHostIdIdx.get(key);
            if (oid != null) {
                ODocument forum = (ODocument) oid.getRecord();
                // update references from parent and children
                ODocument parent = forum.field("parent");
                if(parent != null) {
                    Set children = parent.field("children");
                    if(children != null && children.size() > 0) {
                        children.remove(forum);
                    }
                    parent.save();
                }
                Set<ODocument> children = forum.field("children");
                if(children != null && children.size() > 0) {
                    for(ODocument child: children) {
                        if(child != null) {
                            child.removeField("parent");
                            child.save();
                        }

                    }
                }
                forum.delete();
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

    protected ODocument updForum(Map<String, Object> data) throws Exception {
        ODocument forum = null;
        // update parent according to children and update children according to parent.
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> forumHostIdIdx = db.getMetadata().getIndexManager().getIndex("forumHostIdIdx");
            OCompositeKey key = new OCompositeKey(data.get("host"), data.get("id"));
            OIdentifiable oid = (OIdentifiable) forumHostIdIdx.get(key);
            if (oid != null) {
                forum = (ODocument) oid.getRecord();
                if(data.get("desc") != null) {
                    forum.field("desc", data.get("desc"));
                } else {
                    forum.removeField("desc");
                }
                if(data.get("attributes") != null) {
                    forum.field("attributes", data.get("attributes"));
                } else {
                    forum.removeField("attributes");
                }
                if(data.get("parent") != null) {
                    OCompositeKey parentKey = new OCompositeKey(data.get("host"), data.get("parent"));
                    OIdentifiable parentOid = (OIdentifiable) forumHostIdIdx.get(parentKey);
                    if(parentOid != null) {
                        ODocument parent = (ODocument)parentOid.getRecord();
                        forum.field("parent", parent);
                        // update parent with the children
                        Set children = parent.field("children");
                        if(children != null) {
                            children.add(forum);
                        } else {
                            children = new HashSet<ODocument>();
                            children.add(forum);
                            parent.field("children", children);
                        }
                        parent.save();
                    }
                } else {
                    ODocument parent = forum.field("parent");
                    if(parent != null) {
                        Set children = parent.field("children");
                        children.remove(forum);
                        forum.removeField("parent");
                    }
                }
                List<String> list = (List)data.get("children");
                if(list != null && list.size() > 0) {
                    Set<ODocument> storedChildren = forum.field("children");
                    if(storedChildren != null && storedChildren.size() > 0) {
                        // both lists are not empty. comparison is needed.
                        // first populate inputChildren as ODocument
                        Set<ODocument> inputChildren = new HashSet<ODocument>();
                        for(String id: list) {
                            OCompositeKey childKey = new OCompositeKey(data.get("host"), id);
                            OIdentifiable childOid = (OIdentifiable) forumHostIdIdx.get(childKey);
                            if(childOid != null) inputChildren.add((ODocument)childOid.getRecord());
                        }

                        Set<ODocument> addSet = new HashSet<ODocument>(inputChildren);
                        Set<ODocument> delSet = new HashSet<ODocument>(storedChildren);

                        addSet.removeAll(storedChildren);
                        if(addSet.size() > 0) {
                            for(ODocument doc: addSet) {
                                doc.field("parent", forum);
                                storedChildren.add(doc);
                                doc.save();
                                forum.save();
                            }
                        }
                        delSet.removeAll(inputChildren);
                        if(delSet.size() > 0) {
                            for(ODocument doc: delSet) {
                                doc.removeField("parent");
                                storedChildren.remove(doc);
                                doc.save();
                                forum.save();
                            }
                        }
                    } else {
                        // forum doesn't have children
                        storedChildren = new HashSet<ODocument>();
                        for(String id: (List<String>)data.get("children")) {
                            OCompositeKey childKey = new OCompositeKey(data.get("host"), id);
                            OIdentifiable childOid = (OIdentifiable) forumHostIdIdx.get(childKey);
                            if(childOid != null) {
                                ODocument child = childOid.getRecord();
                                if(child != null) {
                                    storedChildren.add((ODocument)childOid.getRecord());
                                    child.field("parent", forum);
                                    child.save();
                                }
                            }
                        }
                        forum.field("children", storedChildren);
                    }
                } else {
                    Set<ODocument> children = forum.field("children");
                    if(children != null && children.size() > 0) {
                        for(ODocument doc: children) {
                            doc.removeField("parent");
                        }
                    }
                    forum.removeField("children");
                }
                forum.field("updateDate", data.get("updateDate"));
                forum.field("updateUserId", data.get("updateUserId"));
                forum.save();
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
        return forum;
    }

    protected String getForumTree(String host) {
        String json = null;
        String sql = "SELECT FROM Forum WHERE host = ? and parent IS NULL ORDER BY id";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> forums = db.command(query).execute(host);
            if(forums.size() > 0) {
                json = OJSONWriter.listToJSON(forums, "fetchPlan:*:-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return json;
    }

    protected String getForumPost(Map<String, Object> data) {
        String json = null;
        String sql = "select from (traverse posts, children from (select from forum where host = ? and id=?)) where @class = 'Post'";
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

    protected String getForum(String host) {
        String json = null;
        String sql = "SELECT FROM Forum WHERE host = ? ORDER BY createDate";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> forums = db.command(query).execute(host);
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

    protected String getForumDropdown(String host) {
        String json = null;
        String sql = "SELECT FROM Forum WHERE host = ? ORDER BY id";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> forums = db.command(query).execute(host);
            if(forums.size() > 0) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for(ODocument doc: forums) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("id", (String)doc.field("id"));
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

    protected ODocument addPost(Map<String, Object> data) throws Exception {
        ODocument post = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            post = new ODocument(schema.getClass("Post"));
            post.field("host", data.get("host"));
            post.field("id", data.get("id"));
            post.field("title", data.get("title"));
            if(data.get("content") != null) post.field("content", data.get("content"));
            post.field("createDate", data.get("createDate"));
            post.field("createUserId", data.get("createUserId"));
            // parent
            OIndex<?> forumHostIdIdx = db.getMetadata().getIndexManager().getIndex("forumHostIdIdx");
            OCompositeKey parentKey = new OCompositeKey(data.get("host"), data.get("parentId"));
            OIdentifiable parentOid = (OIdentifiable) forumHostIdIdx.get(parentKey);
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
}
