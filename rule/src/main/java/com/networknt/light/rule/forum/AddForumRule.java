package com.networknt.light.rule.forum;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.user.AbstractUserRule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.HashUtil;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 26/11/14.
 */
public class AddForumRule extends AbstractForumRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
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
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("forumAdmin")) {
                error = "Role owner or admin or forumAdmin is required to add forum";
                inputMap.put("responseCode", 401);
            } else {
                String userHost = (String)user.get("host");
                if(userHost != null && !userHost.equals(host)) {
                    error = "User can only add forum from host: " + host;
                    inputMap.put("responseCode", 401);
                } else {
                    ODocument forum = getODocumentByHostId("forumHostIdIdx", host, id);
                    if(forum == null) {
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
                        error = "Forum with id " + id + " exists on host " + host;
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
}
