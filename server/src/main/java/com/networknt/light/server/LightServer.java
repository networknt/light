package com.networknt.light.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.RuleEngine;
import com.networknt.light.server.handler.*;
import com.networknt.light.util.ServiceLocator;
import com.networknt.light.util.Util;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.NameVirtualHostHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.builder.PredicatedHandlersParser;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.util.Headers;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import static io.undertow.Handlers.resource;
import static io.undertow.Handlers.websocket;


public class LightServer {

    static final Logger logger = LoggerFactory.getLogger(LightServer.class);

    static protected boolean shutdownRequested = false;
    public static final Pattern WEBSOCKET_ALLOWED_ORIGIN_HEADER = Pattern.compile("^https?://(localhost|127\\.\\d+\\.\\d+\\.\\d+):\\d+$");
    static Undertow server = null;

    public static void main(final String[] args) {
        logger.info("server starts");
        // add shutdown hook here.
        addDaemonShutdownHook();
        start();
    }

    static public void start() {
        // hosts and server configuration
        Map<String, Object> hostMap = ServiceLocator.getInstance().getHostMap();

        // check if database exists, if not create it and init it.
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(ServiceLocator.getInstance().getDbUrl());
        if(!db.exists()) {
            db.create();
            db.getStorage().getConfiguration().dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
            db.getStorage().getConfiguration().setTimeZone(TimeZone.getTimeZone("UTC"));
            db.getStorage().getConfiguration().update();
            db.close();
            InitDatabase.initDb();
            // replay all the event to create database image.
            // TODO need to rethink replay as orientdb is used instead of Memory Image.
            // TODO load menu and form other common things from db to cache. or just by replay.
            replayEvent();
        }

        NameVirtualHostHandler virtualHostHandler = new NameVirtualHostHandler();
        Iterator<String> it = hostMap.keySet().iterator();
        while (it.hasNext()) {
            String host = it.next();
            Map<String, String> hostPropMap = (Map<String, String>)hostMap.get(host);
            String base = hostPropMap.get("base");
            String transferMinSize = hostPropMap.get("transferMinSize");
            virtualHostHandler
                    .addHost(
                            host,
                            Handlers.predicates(
                                //PredicatedHandlersParser.parse("not path-suffix['.js', '.html', '.css'] -> rewrite['/index.html']"
                                PredicatedHandlersParser.parse("path-prefix['/page', '/form'] -> rewrite['/index.html']"
                                , LightServer.class.getClassLoader()),
                                    new PathHandler(resource(new FileResourceManager(
                                            new File(base), Integer
                                            .valueOf(transferMinSize))))
                                            .addPrefixPath("/api/rs",
                                                    new EagerFormParsingHandler().setNext(
                                                            new RestHandler()))
                                            .addPrefixPath("/api/ws",
                                                    websocket(new WebSocketHandler()))
                            ));
        }
        String ip = ServiceLocator.getInstance().getIp();
        String port = ServiceLocator.getInstance().getPort();
        server = Undertow
                .builder()
                .addHttpListener(Integer.valueOf(port), ip)
                .setBufferSize(1024 * 16)
                .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false)
                .setHandler(
                        Handlers.header(virtualHostHandler,
                                Headers.SERVER_STRING, "LIGHT"))
                .setWorkerThreads(200).build();
        server.start();

    }

    static public void stop() {
        server.stop();
        RuleEngine.getInstance().shutdown();
    }
    // implement shutdown hook here.
    static public void shutdown()
    {
        stop();
        logger.info("Cleaning up before server shutdown");
    }

    static protected void addDaemonShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook( new Thread() { public void run() { LightServer.shutdown(); }});
    }

    /**
     * This replay is to initialize database when the db is newly created. It load rules, forms and pages
     * and certain setup that need to make the applications working. To replay the entire event schema,
     * you have to call it from the Db Admin interface.
     *
     */
    static private void replayEvent() {

        // need to replay from a file instead if the file exist. Otherwise, load from db and replay.
        // in this case, we need to recreate db.

        StringBuilder sb = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("initdb.json");
        try (Scanner scanner = new Scanner(is)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                sb.append(line).append("\n");
            }
            scanner.close();

            ObjectMapper mapper = new ObjectMapper();
            if(sb.length() > 0) {
                // if you want to generate the initdb.json from your dev env, then you should make this
                // file as empty in server resources folder and load all objects again.
                List<Map<String, Object>> events = mapper.readValue(sb.toString(),
                        new TypeReference<List<HashMap<String, Object>>>() {});

                // replay event one by one.
                for(Map<String, Object> event: events) {
                    RuleEngine.getInstance().executeRule(Util.getEventRuleId(event), event);
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        }
    }
}
