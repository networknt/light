package com.networknt.light.rule.file;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.networknt.light.util.Util;
import com.sun.jna.platform.FileUtils;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 2/26/2016.
 */
public abstract class AbstractFileRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractFileRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    protected boolean addFolder(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        // here we get user's host to decide which domain he/she can access
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String host = (String) user.get("host");
        if(host == null) {
            host = (String)data.get("host");
        }
        String path = (String) data.get("path");
        String folder = (String) data.get("folder");
        File file = new File(getAbsPath(getRootPath(host), path) + "/" + folder);
        if(!file.exists()) {
            file.mkdir();
        }
        return true;
    }

    protected boolean renFile(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        // here we get user's host to decide which domain he/she can access
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String host = (String) user.get("host");
        if(host == null) {
            host = (String)data.get("host");
        }
        String path = (String) data.get("path");
        String oldName = (String) data.get("oldName");
        String newName = (String) data.get("newName");

        File oldFile = new File(getAbsPath(getRootPath(host), path) + "/" + oldName);
        File newFile = new File(getAbsPath(getRootPath(host), path) + "/" + newName);
        Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return true;
    }

    public boolean delFile(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        // here we get user's host to decide which domain he/she can access
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String host = (String) user.get("host");
        if(host == null) {
            host = (String)data.get("host");
        }
        String path = (String) data.get("path");
        boolean isdir = (boolean)data.get("isdir");
        if(isdir) {
            File folder = new File(getAbsPath(getRootPath(host), path));
            deleteDir(folder);
        } else {
            File file = new File(getAbsPath(getRootPath(host), path));
            file.delete();
        }
        return true;
    }

    public boolean uplFile(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        // here we get user's host to decide which domain he/she can access
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String host = (String) user.get("host");
        if(host == null) {
            host = (String)data.get("host");
        }
        String name = (String) data.get("name");
        String path = (String) data.get("path");
        String content = (String) data.get("content");
        byte[] bytes = DatatypeConverter.parseBase64Binary(content);
        String p = getAbsPath(getRootPath(host), path);
        File outputFile = new File(p + "/" + name);
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(bytes, 0, bytes.length);
        fos.flush();
        fos.close();
        return true;
    }

    public String getFile(String host, String path) throws Exception {
        String json  = null;
        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        List<Map<String, Object>> children = new ArrayList();
        String root = getRootPath(host);
        File folder = new File(getAbsPath(root, path));
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            Map<String, Object> fileMap = new HashMap<>();
            if (listOfFiles[i].isFile()) {
                fileMap.put("isdir", false);
            } else if (listOfFiles[i].isDirectory()) {
                fileMap.put("isdir", true);
            }
            fileMap.put("name", listOfFiles[i].getName());
            fileMap.put("path", getRelPath(root,listOfFiles[i].getPath()));
            fileMap.put("size", listOfFiles[i].length());
            fileMap.put("time", listOfFiles[i].lastModified());
            children.add(fileMap);
        }
        result.put("children", children);
        json = mapper.writeValueAsString(result);
        return json;
    }

    protected String getRootPath(String host) {
        // get the root folder of host file system.
        Map<String, Object> map = (Map<String, Object>)ServiceLocator.getInstance().getHostMap().get(host);
        return (String)map.get("base");
    }

    protected String getAbsPath(String root, String path) throws Exception {
        String p;
        if(path.equals(".")) {
            p = root;
        } else {
            p = root + "/" + path;
        }
        return p;
    }

    protected String getRelPath(String root, String absPath) {
        return absPath.substring(root.length() + 1);
    }

    protected void writeToOutputStream(File file, OutputStream oos) throws Exception {
        byte[] buf = new byte[8192];
        InputStream is = new FileInputStream(file);
        int c = 0;
        while ((c = is.read(buf, 0, buf.length)) > 0) {
            oos.write(buf, 0, c);
            oos.flush();
        }
        oos.close();
        is.close();
    }

    protected void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}
