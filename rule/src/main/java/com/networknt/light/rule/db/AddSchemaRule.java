package com.networknt.light.rule.db;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.form.AbstractFormRule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 10/12/14.
 *
 */

/*
{
  "name": "Blog",
  "default-cluster-id": 23,
  "cluster-ids": [
    23
  ],
  "cluster-selection": "round-robin",
  "properties": [
    {
      "name": "attributes",
      "type": "EMBEDDEDMAP",
      "collate": "default"
    },
    {
      "name": "children",
      "type": "LINKSET",
      "collate": "default"
    },
    {
      "name": "createDate",
      "type": "DATETIME",
      "collate": "default"
    },
    {
      "name": "createUserId",
      "type": "STRING",
      "collate": "default"
    },
    {
      "name": "desc",
      "type": "STRING",
      "collate": "default"
    },
    {
      "name": "host",
      "type": "STRING",
      "collate": "default"
    },
    {
      "name": "id",
      "type": "STRING",
      "collate": "default"
    },
    {
      "name": "parent",
      "type": "LINK",
      "collate": "default"
    },
    {
      "name": "posts",
      "type": "LINKLIST",
      "collate": "default"
    },
    {
      "name": "updateDate",
      "type": "DATETIME",
      "collate": "default"
    },
    {
      "name": "updateUserId",
      "type": "STRING",
      "collate": "default"
    }
  ]
}
*/
public class AddSchemaRule extends AbstractFormRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String className = (String)data.get("className");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("dbAdmin")) {
                error = "Role owner or admin or dbAdmin is required to add schema";
                inputMap.put("responseCode", 401);
            } else {
                String host = (String)user.get("host");
                if(host != null) {
                    if(!host.equals(data.get("host"))) {
                        error = "User can only add schema from host: " + host;
                        inputMap.put("responseCode", 401);
                    } else {
                        // check if the schema exists in db.
                        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
                        OSchema schema = db.getMetadata().getSchema();
                        if (schema.existsClass(host + "." + className)) {
                            error = "Schema with the id " + host + "." + className + " exists";
                            inputMap.put("responseCode", 400);
                        } else {


                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.putAll((Map<String, Object>)inputMap.get("data"));
                            eventData.put("createDate", new java.util.Date());
                            eventData.put("createUserId", user.get("userId"));
                        }
                    }
                } else {
                    // this is owner adding common schema
                    ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
                    OSchema schema = db.getMetadata().getSchema();
                    if (schema.existsClass(className)) {
                        error = "Schema with the id " + className + " exists";
                        inputMap.put("responseCode", 400);
                    } else {
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.putAll((Map<String, Object>)inputMap.get("data"));
                        eventData.put("createDate", new java.util.Date());
                        eventData.put("createUserId", user.get("userId"));
                        // remove host from data as this is owner adding role
                        eventData.remove("host");
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
