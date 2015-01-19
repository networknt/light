package com.networknt.light.rule.host;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Service;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve Hu on 2015-01-19.
 */
public abstract class AbstractHostRule extends AbstractRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(ServiceLocator.class);
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public abstract boolean execute (Object ...objects) throws Exception;

    protected void addHost(Map<String, Object> data) throws Exception {
        Map<String, Object> hostMap = ServiceLocator.getInstance().getHostMap();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("base", data.get("base"));
        map.put("transferMinSize", data.get("transferMinSize"));
        hostMap.put((String)data.get("id"), map);
        writeHostMap(hostMap);
    }

    protected void updHost(Map<String, Object> data) throws Exception {
        Map<String, Object> hostMap = ServiceLocator.getInstance().getHostMap();
        Map<String, Object> map = (Map<String, Object>)hostMap.get(data.get("id"));
        map.put("base", data.get("base"));
        map.put("transferMinSize", data.get("transferMinSize"));
        writeHostMap(hostMap);
    }

    protected void delHost(Map<String, Object> data) throws Exception {
        Map<String, Object> hostMap = ServiceLocator.getInstance().getHostMap();
        hostMap.remove(data.get("id"));
        writeHostMap(hostMap);
    }

    private void writeHostMap(Map<String, Object> hostMap) {
        try {
            mapper.writeValue((new File(System.getProperty("user.home") + "/virtualhost.json")), hostMap);
        } catch (IOException ioe) {
            logger.error("Exception:", ioe);
        }
    }
}
