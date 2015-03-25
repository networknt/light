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

package com.networknt.light.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.RuleEngine;
import com.networknt.light.rule.rule.AbstractRuleRule;
import com.networknt.light.server.handler.RestHandler;
import com.networknt.light.server.handler.WebSocketHandler;
import com.networknt.light.util.ServiceLocator;
import com.networknt.light.util.Util;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.NameVirtualHostHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.builder.PredicatedHandlersParser;
import io.undertow.server.handlers.form.EagerFormParsingHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.util.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.Service;
import java.io.File;
import java.io.InputStream;
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

        OrientGraphFactory factory = ServiceLocator.getInstance().getFactory();
        // check if database exists, if not create it and init it.
        if(!factory.exists()) {
            try {
                OrientBaseGraph g = new OrientGraph(ServiceLocator.getInstance().getDbUrl());
                // database is auto created
                g.command(new OCommandSQL("alter database custom useLightweightEdges=true")).execute();
                g.command(new OCommandSQL("alter database DATETIMEFORMAT yyyy-MM-dd'T'HH:mm:ss.SSS")).execute();
                g.command(new OCommandSQL("alter database TIMEZONE UTC")).execute();
                OGlobalConfiguration.RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue(-1);
            } finally {
                // this also closes the OrientGraph instances created by the factory
                // Note that OrientGraphFactory does not implement Closeable
                factory.close();
            }
            InitDatabase.initDb();
            // load rule compileCache here
            AbstractRuleRule.loadCompileCache();
            // replay all the event to create database image.
            // TODO need to rethink replay as orientdb is used instead of Memory Image.
            replayEvent();
        } else {
            // load rule compileCache here
            AbstractRuleRule.loadCompileCache();
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

            ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
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
