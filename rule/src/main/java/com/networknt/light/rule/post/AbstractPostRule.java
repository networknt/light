package com.networknt.light.rule.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.*;

/**
 * Created by steve on 27/11/14.
 */
public abstract class AbstractPostRule extends AbstractRule implements Rule {

    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    public abstract boolean execute (Object ...objects) throws Exception;

    protected ODocument addPost(Map<String, Object> data) throws Exception {
        ODocument post = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            post = new ODocument(schema.getClass("Post"));
            post.field("id", data.get("id"));
            post.field("host", data.get("host"));
            post.field("title", data.get("title"));
            if(data.get("source") != null) post.field("source", data.get("source"));
            if(data.get("content") != null) post.field("content", data.get("content"));
            post.field("createDate", data.get("createDate"));
            post.field("createUserId", data.get("createUserId"));
            // update parent children list, assuming parent has index with class + HostIdIdx.
            OIndex<?> hostIdIdx = db.getMetadata().getIndexManager().getIndex(((String)data.get("parentClassName")).toLowerCase() + "HostIdIdx");
            OCompositeKey key = new OCompositeKey(data.get("parentHost"), data.get("parentId"));
            OIdentifiable oid = (OIdentifiable) hostIdIdx.get(key);
            if (oid != null) {
                ODocument doc = (ODocument) oid.getRecord();
                post.field("parent", doc);
                Set children = doc.field("children");
                children.add(post);
                doc.save();
            }
            // update tags
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
            // synch counter in the same transaction. It is used for playback.
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

    protected boolean delPost(Map<String, Object> data) throws Exception {
        boolean result = false;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> postIdIdx = db.getMetadata().getIndexManager().getIndex("Post.id");
            OIdentifiable oid = (OIdentifiable) postIdIdx.get(data.get("id"));
            if (oid != null) {
                ODocument post = (ODocument) oid.getRecord();
                // remove from parent children set.
                OIndex<?> hostIdIdx = db.getMetadata().getIndexManager().getIndex(((String)data.get("parentClassName")).toLowerCase() + "HostIdIdx");
                OCompositeKey parentKey = new OCompositeKey(data.get("parentHost"), data.get("parentId"));
                OIdentifiable parentOid = (OIdentifiable) hostIdIdx.get(parentKey);
                if (parentOid != null) {
                    ODocument parent = (ODocument) parentOid.getRecord();
                    Set children = parent.field("children");
                    children.remove(post);
                    parent.save();
                }
                // remove all children
                List<ODocument> children = post.field("children");
                if(children != null && children.size() > 0) {
                    for(ODocument child: children) {
                        child.delete();
                    }
                }
                // remove post reference from tags.
                // TODO iterate valueSet should be faster
                Map<String, Object> tagMap = post.field("tags");
                if(tagMap != null && tagMap.size() > 0) {
                    Set<String> tagSet = tagMap.keySet();
                    String host = post.field("host");
                    String className = post.getClassName();
                    for(String tagName: tagSet) {
                        ODocument tag = null;
                        // get the tag is it exists
                        OIndex<?> hostNameClassIdx = db.getMetadata().getIndexManager().getIndex("hostNameClassIdx");
                        OCompositeKey key = new OCompositeKey(host, tagName, className);
                        OIdentifiable oIdentifiable = (OIdentifiable) hostNameClassIdx.get(key);
                        if (oIdentifiable != null) {
                            tag = (ODocument) oIdentifiable.getRecord();
                            Set links = tag.field("links");
                            links.remove(post);
                            tag.save();
                        }
                    }
                }
                post.delete();
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


    protected ODocument updPost(Map<String, Object> data) throws Exception {
        ODocument post = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> postIdIdx = db.getMetadata().getIndexManager().getIndex("Post.id");
            OIdentifiable oid = (OIdentifiable) postIdIdx.get(data.get("id"));
            if (oid != null) {
                post = (ODocument) oid.getRecord();
                // can only update parent, title, source, content and tags here
                String title = (String)data.get("title");
                if(title != null) {
                    if(!title.equals(post.field("title"))) {
                        post.field("title", title);
                    }
                } else {
                    post.removeField("title");
                }
                String source = (String)data.get("source");
                if(source != null) {
                    if(!source.equals(post.field("source"))) {
                        post.field("source", source);
                    }
                } else {
                    post.removeField("source");
                }
                String content = (String)data.get("content");
                if(content != null) {
                    if(!content.equals(post.field("content"))) {
                        post.field("content", content);
                    }
                } else {
                    post.removeField("content");
                }
                // update parent
                ODocument currentParent = post.field("parent");
                OIndex<?> hostIdIdx = db.getMetadata().getIndexManager().getIndex(((String)data.get("parentClassName")).toLowerCase() + "HostIdIdx");
                OCompositeKey parentKey = new OCompositeKey(data.get("parentHost"), data.get("parentId"));
                OIdentifiable parentOid = (OIdentifiable) hostIdIdx.get(parentKey);
                if(parentOid != null) {
                    ODocument parent = (ODocument)parentOid.getRecord();
                    if(currentParent == null || !currentParent.field("id").equals(parent.field("id"))) {
                        post.field("parent", parent);
                    }
                }
                // update tags
                String host = post.field("host");
                String className = post.getClassName();
                OSchema schema = db.getMetadata().getSchema();
                OIndex<?> hostNameClassIdx = db.getMetadata().getIndexManager().getIndex("hostNameClassIdx");
                Map<String, Object> tagMap = post.field("tags");
                if(tagMap != null && tagMap.size() > 0) {
                    // There are existing tags.
                    if(data.get("tags") != null) {
                        // both are not null, we need a comparison to figure out what to do.
                        Set<String> inputTags = new HashSet<String>(Arrays.asList(((String)data.get("tags")).split("\\s*,\\s*")));
                        Set<String> storedTags = tagMap.keySet();
                        Set<String> addSet = new HashSet<String>(inputTags);
                        Set<String> delSet = new HashSet<String>(storedTags);
                        addSet.removeAll(storedTags);
                        if(addSet.size() > 0) {
                            // there are some newly added tags
                            for(String tagName: addSet) {
                                ODocument tag = null;
                                // get the tag is it exists
                                OCompositeKey key = new OCompositeKey(host, tagName, className);
                                OIdentifiable oIdentifiable = (OIdentifiable) hostNameClassIdx.get(key);
                                if (oIdentifiable != null) {
                                    tag = (ODocument) oIdentifiable.getRecord();
                                    Set links = tag.field("links");
                                    links.add(post);
                                    tag.save();
                                } else {
                                    tag = new ODocument(schema.getClass("Tag"));
                                    tag.field("host", host);
                                    tag.field("name", tagName);
                                    tag.field("class", className);
                                    tag.field("createDate", data.get("updateDate"));
                                    tag.field("createUserId", data.get("updateUserId"));
                                    Set links = new HashSet<Object>();
                                    links.add(post);
                                    tag.field("links", links);
                                    tag.save();
                                }
                                tagMap.put(tagName, tag);
                            }
                            post.field("tags", tagMap);
                            post.save();
                        }
                        delSet.removeAll(inputTags);
                        if(delSet.size() > 0) {
                            // there are some removed tags
                            for(String tagName: delSet) {
                                ODocument tag = null;
                                // get the tag is it exists
                                OCompositeKey key = new OCompositeKey(host, tagName, className);
                                OIdentifiable oIdentifiable = (OIdentifiable) hostNameClassIdx.get(key);
                                if (oIdentifiable != null) {
                                    tag = (ODocument) oIdentifiable.getRecord();
                                    Set links = tag.field("links");
                                    links.remove(post);
                                    tag.save();
                                }
                                tagMap.remove(tagName);
                            }
                            post.field("tags", tagMap);
                            post.save();
                        }
                    } else {
                        // remove the post reference from all tags as input is empty but stored has values.
                        // and then remove the tags from blog.
                        Iterator it = tagMap.entrySet().iterator();
                        while(it.hasNext()) {
                            ORecordId tagRid = (ORecordId)it.next();
                            ODocument tag = db.load(tagRid);
                            Set links = tag.field("links");
                            links.remove(post);
                            tag.save();
                        }
                        post.removeField("tags");
                        post.save();
                    }
                } else {
                    // tagMap is null
                    if(data.get("tags") != null) {
                        tagMap = new HashMap<String, Object>();
                        Set<String> inputTags = new HashSet<String>(Arrays.asList(((String)data.get("tags")).split("\\s*,\\s*")));
                        for(String tagName: inputTags) {
                            ODocument tag = null;
                            // get the tag if it exists
                            OCompositeKey key = new OCompositeKey(host, tagName, className);
                            OIdentifiable oIdentifiable = (OIdentifiable) hostNameClassIdx.get(key);
                            if (oIdentifiable != null) {
                                tag = (ODocument) oIdentifiable.getRecord();
                                Set links = tag.field("links");
                                links.add(post);
                                tag.save();
                            } else {
                                tag = new ODocument(schema.getClass("Tag"));
                                tag.field("host", host);
                                tag.field("name", tagName);
                                tag.field("class", className);
                                tag.field("createDate", data.get("updateDate"));
                                tag.field("createUserId", data.get("updateUserId"));
                                Set links = new HashSet<String>();
                                links.add(post);
                                tag.field("links", links);
                                tag.save();
                            }
                            tagMap.put(tagName, tag);
                        }
                        post.field("tags", tagMap);
                        post.save();
                    }
                }
                post.field("updateDate", data.get("updateDate"));
                post.field("updateUserId", data.get("updateUserId"));
                post.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return post;
    }


    protected ODocument updBlogDb(Map<String, Object> data, String userRid, String userId) throws Exception {
        ODocument blog = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            blog = db.load(new ORecordId((String)data.get("@rid")));
            if (blog != null) {
                String title = (String)data.get("title");
                if(title != null && !title.equals(blog.field("title"))) {
                    blog.field("title", title);
                }
                String source = (String)data.get("source");
                if(source != null && !source.equals(blog.field("source"))) {
                    blog.field("source", source);
                }
                String summary = (String)data.get("summary");
                if(summary != null && !summary.equals(blog.field("content"))) {
                    blog.field("summary", summary);
                }
                String content = (String)data.get("content");
                if(content != null && !content.equals(blog.field("content"))) {
                    blog.field("content", content);
                }

                // update tags
                String host = blog.field("host");
                String className = blog.field("@class");
                Map<String, Object> tagMap = blog.field("tags");
                if(tagMap != null && tagMap.size() > 0) {
                    // There are existing tags.
                    if(data.get("tags") != null) {
                        // both are not null, we need a comparison to figure out what to do.
                        Set<String> inputTags = new HashSet<String>(Arrays.asList(((String)data.get("tags")).split("\\s*,\\s*")));
                        Set<String> storedTags = tagMap.keySet();
                        Set<String> addSet = new HashSet<String>(inputTags);
                        Set<String> delSet = new HashSet<String>(storedTags);
                        addSet.removeAll(storedTags);
                        if(addSet.size() > 0) {
                            for(String tagName: addSet) {
                                ODocument tag = null;
                                // get the tag is it exists
                                OIndex<?> hostNameClassIdx = db.getMetadata().getIndexManager().getIndex("hostNameClassIdx");
                                // this is a unique index, so it retrieves a OIdentifiable
                                OCompositeKey key = new OCompositeKey(host, tagName, className);
                                OIdentifiable oIdentifiable = (OIdentifiable) hostNameClassIdx.get(key);
                                if (oIdentifiable != null) {
                                    tag = (ODocument) oIdentifiable.getRecord();
                                    Set links = tag.field("links");
                                    links.add(blog.field("@rid"));
                                    tag.save();
                                    db.commit();
                                } else {
                                    tag = new ODocument(schema.getClass("Tag"));
                                    tag.field("host", host);
                                    tag.field("name", tagName);
                                    tag.field("class", className);
                                    tag.field("createDate", new java.util.Date());
                                    tag.field("createUserRid", new ORecordId(userRid));
                                    Set links = new HashSet<Object>();
                                    links.add(blog.field("@rid"));
                                    tag.field("links", links);
                                    tag.save();
                                    db.commit();
                                }
                                tagMap.put(tagName, tag.field("@rid"));
                            }
                            blog.field("tags", tagMap);
                            blog.save();
                            db.commit();
                        }
                        delSet.removeAll(inputTags);
                        if(delSet.size() > 0) {
                            // remove the tags
                            for(String tagName: delSet) {
                                ODocument tag = null;
                                // get the tag is it exists
                                OIndex<?> hostNameClassIdx = db.getMetadata().getIndexManager().getIndex("hostNameClassIdx");
                                // this is a unique index, so it retrieves a OIdentifiable
                                OCompositeKey key = new OCompositeKey(host, tagName, className);
                                OIdentifiable oIdentifiable = (OIdentifiable) hostNameClassIdx.get(key);
                                if (oIdentifiable != null) {
                                    tag = (ODocument) oIdentifiable.getRecord();
                                    Set links = tag.field("links");
                                    links.remove(blog.field("@rid"));
                                    tag.save();
                                    db.commit();
                                }
                                tagMap.remove(tagName);
                            }
                            blog.field("tags", tagMap);
                            blog.save();
                            db.commit();
                        }
                    } else {
                        // remove the blog reference from all tags as input is empty but stored has values.
                        // and then remove the tags from blog.
                        Iterator it = tagMap.entrySet().iterator();
                        while(it.hasNext()) {
                            ORecordId tagRid = (ORecordId)it.next();
                            ODocument tag = db.load(tagRid);
                            Set links = tag.field("links");
                            links.remove(blog.field("@rid"));
                            tag.save();
                        }
                        blog.removeField("tags");
                        blog.save();
                        db.commit();
                    }
                } else {
                    // tagMap is null
                    if(data.get("tags") != null) {
                        tagMap = new HashMap<String, Object>();
                        Set<String> inputTags = new HashSet<String>(Arrays.asList(((String)data.get("tags")).split("\\s*,\\s*")));
                        for(String tagName: inputTags) {
                            ODocument tag = null;
                            // get the tag if it exists
                            OIndex<?> hostNameClassIdx = db.getMetadata().getIndexManager().getIndex("hostNameClassIdx");
                            // this is a unique index, so it retrieves a OIdentifiable
                            OCompositeKey key = new OCompositeKey(host, tagName, className);
                            OIdentifiable oIdentifiable = (OIdentifiable) hostNameClassIdx.get(key);
                            if (oIdentifiable != null) {
                                tag = (ODocument) oIdentifiable.getRecord();
                                Set links = tag.field("links");
                                links.add(blog.field("@rid"));
                                tag.save();
                                db.commit();
                            } else {
                                tag = new ODocument(schema.getClass("Tag"));
                                tag.field("host", host);
                                tag.field("name", tagName);
                                tag.field("class", className);
                                tag.field("createDate", new java.util.Date());
                                tag.field("createUserRid", new ORecordId(userRid));
                                Set links = new HashSet<String>();
                                links.add(blog.field("@rid"));
                                tag.field("links", links);
                                tag.save();
                                db.commit();
                            }
                            tagMap.put(tagName, tag.field("@rid"));
                        }
                        blog.field("tags", tagMap);
                        blog.save();
                        db.commit();
                    }
                }
                blog.field("updateDate", new java.util.Date());
                blog.field("updateUserId", userId);
                blog.field("updateUserRid", userRid);
                blog.save();
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return blog;
    }

    protected ODocument upVotePost(Map<String, Object> data) {
        ODocument post = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> postIdIdx = db.getMetadata().getIndexManager().getIndex("Post.id");
            OIdentifiable oid = (OIdentifiable) postIdIdx.get(data.get("id"));
            if (oid != null) {
                post = (ODocument) oid.getRecord();
                if(post != null) {
                    OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
                    OIdentifiable userOid = (OIdentifiable) postIdIdx.get(data.get("updateUserId"));
                    if(userOid != null) {
                        ODocument user = userOid.getRecord();
                        if(user != null) {
                            Set upSet = post.field("upUsers");
                            if(upSet == null) {
                                upSet = new HashSet<String>();
                                upSet.add(user);
                                post.field("upUsers", upSet);
                            } else {
                                upSet.add(user);
                            }
                            // blindly remove the user from downUsers if it is there
                            Set downSet = post.field("downUsers");
                            if(downSet != null) {
                                downSet.remove(user);
                            }
                            post.save();
                            db.commit();
                        }
                    }

                }
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return post;
    }

    protected ODocument downVoteBlog(Map<String, Object> data) {
        ODocument post = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> postIdIdx = db.getMetadata().getIndexManager().getIndex("Post.id");
            OIdentifiable oid = (OIdentifiable) postIdIdx.get(data.get("id"));
            if (oid != null) {
                post = (ODocument) oid.getRecord();
                if(post != null) {
                    OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("User.userId");
                    OIdentifiable userOid = (OIdentifiable) postIdIdx.get(data.get("updateUserId"));
                    if(userOid != null) {
                        ODocument user = userOid.getRecord();
                        if(user != null) {
                            Set downSet = post.field("downUsers");
                            if(downSet == null) {
                                downSet = new HashSet<String>();
                                downSet.add(user);
                                post.field("downUsers", downSet);
                            } else {
                                downSet.add(user);
                            }
                            // blindly remove the user from upUsers if it is there
                            Set upSet = post.field("upUsers");
                            if(upSet != null) {
                                upSet.remove(user);
                            }
                            post.save();
                            db.commit();
                        }
                    }

                }
            }
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
