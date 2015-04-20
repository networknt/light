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

package com.networknt.light.rule.blog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;

/**
 * Created by steve on 08/10/14.
 */
public abstract class AbstractBlogRule extends AbstractRule implements Rule {

    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public abstract boolean execute (Object ...objects) throws Exception;
    /*
    protected String getJsonByRid(String blogRid) {
        // try to get it from cache first.
        String json = null;
        Map<String, Object> blogMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("blogMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)blogMap.get("cache");
        if(cache != null) {
            json = (String)cache.get("blogRid");
        }
        if(json == null) {
            // TODO warning to increase cache if this happens.
            json = DbService.getJsonByRid(blogRid);
            // put it into the blog cache.
            if(json != null) {
                if(cache == null) {
                    cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                            .maximumWeightedCapacity(1000)
                            .build();
                    blogMap.put("cache", cache);
                }
                cache.put(blogRid, json);
            }
        }
        return json;
    }

    protected Map<String, Object> getBlogByHostTitle(String host, String title) {
        Map<String, Object> map = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> hostTitleIdx = db.getMetadata().getIndexManager().getIndex("hostTitleIdx");
            // this is a unique index, so it retrieves a OIdentifiable
            OCompositeKey key = new OCompositeKey(host, title);
            OIdentifiable blog = (OIdentifiable) hostTitleIdx.get(key);
            if (blog != null) {
                String json = ((ODocument) blog.getRecord()).toJSON();
                map = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return map;
    }

    protected ODocument addBlog(Map<String, Object> data) throws Exception {
        ODocument blog = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            OIndex<?> blogHostIdIdx = db.getMetadata().getIndexManager().getIndex("blogHostIdIdx");
            blog = new ODocument(schema.getClass("Blog"));
            blog.field("host", data.get("host"));
            blog.field("id", data.get("id"));
            if(data.get("description") != null) blog.field("description", data.get("description"));
            if(data.get("attributes") != null) blog.field("attributes", data.get("attributes"));
            blog.field("createDate", data.get("createDate"));
            blog.field("createUserId", data.get("createUserId"));
            // parent
            if(data.get("parent") != null) {
                OCompositeKey parentKey = new OCompositeKey(data.get("host"), data.get("parent"));
                OIdentifiable parentOid = (OIdentifiable) blogHostIdIdx.get(parentKey);
                if(parentOid != null) {
                    ODocument parent = (ODocument)parentOid.getRecord();
                    blog.field("parent", parent);
                    // update parent with the children
                    Set children = parent.field("children");
                    if(children != null) {
                        children.add(blog);
                    } else {
                        children = new HashSet<ODocument>();
                        children.add(blog);
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
                    OIdentifiable childOid = (OIdentifiable) blogHostIdIdx.get(childKey);
                    if(childOid != null) {
                        ODocument child = (ODocument)childOid.getRecord();
                        children.add(child);
                        child.field("parent", blog);
                        child.save();
                    }
                }
                blog.field("children", children);
            }
            blog.save();
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
    */
    /*
    protected void addBlog(Map<String, Object> data) throws Exception {
        ODocument blog = addBlogDb(data);
        // rebuild cache in memory.
        Map<String, Object> blogMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("blogMap");
        // update central cache
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)blogMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            blogMap.put("cache", cache);
        }
        cache.put(blog.field("@rid").toString(), blog.toJSON());
        // update newList
        String host = (String)data.get("host");
        List<String> newList = (List<String>)blogMap.get(host + "newList");
        if(newList == null) {
            newList = new ArrayList<String>();
            newList.add(blog.field("@rid").toString());
            blogMap.put(host + "newList", newList);
        } else {
            newList.add(0, blog.field("@rid").toString());  // add the head of the list.
        }
        // TODO build hot list

    }
    */
    /*
    protected ODocument addBlogDb(Map<String, Object> data) throws Exception {
        ODocument blog = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();

        try {
            db.begin();
            blog = new ODocument(schema.getClass("Blog"));
            blog.field("host", data.get("host"));
            blog.field("title", data.get("title"));
            blog.field("source", data.get("source"));
            blog.field("summary", data.get("summary"));
            blog.field("content", data.get("content"));
            blog.field("rank", 0);
            java.util.Date d = new java.util.Date();
            blog.field("createDate", data.get("createDate"));
            blog.field("updateDate", data.get("createDate"));
            blog.field("createUserId", data.get("createUserId"));
            blog.save();
            db.commit();
            Map<String, Object> tagMap = new HashMap<String, Object>();

            Set<String> inputTags = data.get("tags") != null? new HashSet<String>(Arrays.asList(((String)data.get("tags")).split("\\s*,\\s*"))) : new HashSet<String>();
            String host = blog.field("host");
            String className = blog.field("@class");
            for(String tagName: inputTags) {
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
                    tag.field("createDate", data.get("createDate"));
                    tag.field("createUserId", data.get("createUserId"));
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
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return blog;
    }
    */
    /*
    protected boolean delBlog(Map<String, Object> data) throws Exception {
        boolean result = false;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> blogHostIdIdx = db.getMetadata().getIndexManager().getIndex("blogHostIdIdx");
            OCompositeKey key = new OCompositeKey(data.get("host"), data.get("id"));
            OIdentifiable oid = (OIdentifiable) blogHostIdIdx.get(key);
            if (oid != null) {
                ODocument blog = (ODocument) oid.getRecord();
                // update references from parent and children
                ODocument parent = blog.field("parent");
                if(parent != null) {
                    Set children = parent.field("children");
                    if(children != null && children.size() > 0) {
                        children.remove(blog);
                    }
                    parent.save();
                }
                Set<ODocument> children = blog.field("children");
                if(children != null && children.size() > 0) {
                    for(ODocument child: children) {
                        if(child != null) {
                            child.removeField("parent");
                            child.save();
                        }

                    }
                }
                blog.delete();
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
    */
    /*
    protected void delBlogUpdCache(String blogRid, String host) throws Exception {
        delBlogDb(blogRid);
        // rebuild cache in memory.
        Map<String, Object> blogMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("blogMap");
        // update central cache
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)blogMap.get("cache");
        if(cache != null) {
            cache.remove(blogRid);
        }
        // update newList
        List<String> newList = (List<String>)blogMap.get(host + "newList");
        if(newList != null) {
            newList.remove(blogRid);
        }

        // TODO build hot list

    }
    */
    /*
    protected boolean delBlogDb(String blogRid) throws Exception {
        boolean result = false;
        ODocument blog = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            blog = db.load(new ORecordId(blogRid));
            // remove the tags
            Map<String, Object> tagMap = blog.field("tags");
            if(tagMap != null) {
                Iterator it = tagMap.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    ODocument tag = db.load((ORecordId)pairs.getValue());
                    Set links = tag.field("links");
                    links.remove(blog.field("@rid"));
                    tag.save();
                    db.commit();
                }
            }
            db.delete(new ORecordId(blogRid));
            db.commit();
            result = true;
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return result;
    }
    */
    /*
    protected int delBlogByHost(String host) throws Exception {
        int recordsUpdated = 0;
        String sql = "DELETE FROM Blog WHERE host = '" + host + "'";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            recordsUpdated = db.command(new OCommandSQL(sql)).execute();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        if(recordsUpdated > 0) {
            // clean the cache if any.
            Map<String, Object> blogMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("blogMap");
            blogMap.remove(host + "newList");
            // cached blog will be remove the cache automatically.
        }
        return recordsUpdated;
    }
    */
    /*
    protected void updBlog(Map<String, Object> data, String userRid, String userId) throws Exception {
        ODocument blog = updBlogDb(data, userRid, userId);
        String blogRid = blog.field("@rid").toString();
        // rebuild cache in memory.
        Map<String, Object> blogMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("blogMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)blogMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            blogMap.put("cache", cache);
        }
        cache.put(blogRid, blog.toJSON());

        // update newList
        String host = (String)data.get("host");
        List<String> newList = (List<String>)blogMap.get(host + "newList");
        if(newList == null) {
            newList = new ArrayList<String>();
            blogMap.put(host + "newList", newList);
            newList.add(blogRid);
        } else {
            newList.remove(blogRid);
            newList.add(0, blogRid);
        }
        // TODO build hot list

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
    protected long getTotalNumberBlogFromDb(Map<String, Object> criteria) {
        long total = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) as count FROM Blog");

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

    protected List<Map<String, Object>> searchBlogFromDb(Map<String, Object> criteria) {
        List<Map<String,Object>> list = null;
        StringBuilder sql = new StringBuilder("SELECT FROM Blog ");
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
            List<ODocument> result = db.command(query).execute();
            if(result.size() > 0) {
                String json = OJSONWriter.listToJSON(result, null);
                list = mapper.readValue(json, new TypeReference<List<HashMap<String, Object>>>() {});

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return list;
    }
    protected List<ODocument> searchBlogDb(Map<String, Object> criteria) {
        List<ODocument> blogs = null;
        StringBuilder sql = new StringBuilder("SELECT FROM Blog ");
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
            blogs = db.command(query).execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return blogs;
    }

    protected Map<String, Object> refreshCache(String host) {
        Map<String, Object> blogMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("blogMap");
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("host", host);
        criteria.put("sortedBy", "updateDate");
        criteria.put("sortDir", "DESC");
        List<ODocument> blogs = searchBlogDb(criteria);
        List<String> newList = new ArrayList<String>();
        // build new map/list
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)blogMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            blogMap.put("cache", cache);
        }
        int i = 0;
        int pageSize = 2; // TODO get from config
        for (ODocument blog : blogs) {
            // cache the first page for now. most people will read the first page as it contains
            // new posts.
            if(i < pageSize) {
                cache.put(blog.field("@rid").toString(), blog.toJSON());
            }
            newList.add(blog.field("@rid").toString());
        }
        blogMap.put(host + "newList", newList);

        // TODO build hot list
        return blogMap;
    }

    protected ODocument upVoteBlog(String blogRid, String userRid) {
        ODocument blog = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            blog = db.load(new ORecordId(blogRid));
            if (blog != null) {
                Set upSet = blog.field("upUsers");
                if(upSet == null) {
                    upSet = new HashSet<String>();
                    upSet.add(new ORecordId(userRid));
                    blog.field("upUsers", upSet);
                } else {
                    upSet.add(new ORecordId(userRid));
                }
                // remove the user from downUsers if it is there
                // blindly remove
                Set downSet = blog.field("downUsers");
                if(downSet != null) {
                    downSet.remove(new ORecordId(userRid));
                }
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

    protected ODocument downVoteBlog(String blogRid, String userRid) {
        ODocument blog = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            blog = db.load(new ORecordId(blogRid));
            if (blog != null) {
                Set downSet = blog.field("downUsers");
                if(downSet == null) {
                    downSet = new HashSet<String>();
                    downSet.add(new ORecordId(userRid));
                    blog.field("downUsers", downSet);
                } else {
                    downSet.add(new ORecordId(userRid));
                }
                // remove the user from upUsers if it is there
                // blindly remove
                Set upSet = blog.field("upUsers");
                if(upSet != null) {
                    upSet.remove(new ORecordId(userRid));
                }
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
    */
}
