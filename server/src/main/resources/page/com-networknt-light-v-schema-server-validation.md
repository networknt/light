# How does Light Framework validate request based on the JSON schema on server side?

## Overview
Most update APIs to the Light Framework are utilizing [React Schema Form](https://github.com/networknt/react-schema-form)
to collect data from Browser. JSON schema is part of the schema form so the data from browser have been validated by the
schema form before sending to the server; however, we cannot trust the browser side validation as it is easily
manipulated. A server side schema validation is the second defense line and it is a must.

## Shared schemas
Each API on Light Framework maps to a Java class that implements Rule interface. For the rules that
are associated with schema forms, the validation schema will be loaded from schema forms automatically.

Here is the code in impForm to update rule schema and remove the cache.
```
    // According to action in the list, populate validation schema.
    List<Map<String, Object>> actions = form.getProperty("action");
    for(Map<String, Object> action: actions) {
        String ruleClass = Util.getCommandRuleId(action);
        Vertex rule = graph.getVertexByKey("Rule.ruleClass", ruleClass);
        if(rule != null) {
            rule.setProperty("schema", data.get("schema"));
            Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
            ConcurrentMap<String, Map<String, Object>> cache = (ConcurrentMap<String, Map<String, Object>>)ruleMap.get("cache");
            if(cache == null) {
                cache.remove(ruleClass);
            }
        }
    }

```

##