package com.networknt.light.rule.forum;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.user.AbstractUserRule;
import com.networknt.light.server.DbService;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 26/11/14.
 */
public class UpdForumRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
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
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("forumAdmin")) {
                inputMap.put("error", "Role owner or admin or forumAdmin is required to update forum");
                inputMap.put("responseCode", 401);
                return false;
            } else {
                String userHost = (String)user.get("host");
                if(userHost != null && !userHost.equals(host)) {
                    inputMap.put("error", "User can only update forum from host: " + host);
                    inputMap.put("responseCode", 401);
                    return false;
                } else {
                    ODocument forum = null;
                    if(rid != null) {
                        forum = DbService.getODocumentByRid(rid);
                        if(forum != null) {
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.putAll((Map<String, Object>)inputMap.get("data"));
                            eventData.put("host", forum.field("host"));
                            eventData.put("id", forum.field("id"));
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
                                            inputMap.put("error", "Current forum shows up in the Children list");
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
                            inputMap.put("error", "Forum with @rid " + rid + " cannot be found");
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
}
