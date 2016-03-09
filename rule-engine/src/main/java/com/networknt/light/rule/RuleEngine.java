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

package com.networknt.light.rule;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.compiler.CharSequenceCompiler;
import com.networknt.light.rule.compiler.CharSequenceCompilerException;
import com.networknt.light.util.ServiceLocator;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by husteve on 8/1/14.
 */
public class RuleEngine {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RuleEngine.class);

    // build a ruleCache with 10000 rules as max. For large system, this might be even bigger. This cache is thread safe.
    // TODO put this into configuration server.json.
    private ConcurrentLinkedHashMap<String, Rule> ruleCache = new ConcurrentLinkedHashMap.Builder<String, Rule>().maximumWeightedCapacity(10000).build();
    // executor services for execute rules async.
    private ExecutorService executorService = Executors.newFixedThreadPool(3);


    // Create a CharSequenceCompiler instance which is used to compile
    // rule into Java classes which are then used by rule engine.
    // The -target 1.8 options are simply an example of how to pass javac
    // compiler options
    private final CharSequenceCompiler<Rule> compiler = new CharSequenceCompiler<Rule>(
            getClass().getClassLoader(), Arrays.asList(new String[]{"-target", "1.8"}));

    // This eager initialization. Assume rule engine is always used.
    private static final RuleEngine instance = new RuleEngine();
    private RuleEngine() {}
    public static RuleEngine getInstance() {
        return instance;
    }

    /**
     *
     * @param ruleClass
     * @return Rule instance
     */
    Rule loadRule(String ruleClass) {
        Rule rule = ruleCache.get(ruleClass);
        if (rule == null) {
            // check if there are any rules in compileMap which are newly added or imported
            Map<String, Object> compileMap = ServiceLocator.getInstance().getMemoryImage("compileMap");
            ConcurrentMap<String, String> compileCache = (ConcurrentMap<String, String>)compileMap.get("cache");
            if(compileCache != null && compileCache.size() > 0) {
                compileRule(compileCache, ruleCache);
            }
            rule = ruleCache.get(ruleClass);
        }
        return rule;
    }

    public boolean executeRuleAsync(final String ruleClass, final Object ...objects) throws Exception {
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    Rule rule = loadRule(ruleClass);
                    rule.execute(objects);
                } catch (Exception e) {
                    logger.error("Exception:", e);
                }
            }
        });
        return true;
    }

    public boolean executeRule(String ruleClass, Object ...objects) throws Exception {
        if(ruleClass == null || ruleClass.length() == 0) {
            throw new Exception("Invalid rule class name");
        }
        Rule rule = loadRule(ruleClass);
        if(rule == null) {
            logger.error("Could not find rule:" + ruleClass);
            throw new Exception("Could not find rule for " + ruleClass);
        }
        return rule.execute(objects);
    }

    /**
     * Compile rule Java source map and put into ruleCache
     * @param compileCache Map<String, String>
     * @param ruleCache Map<String, Rule>
     *
     */
    void compileRule(Map<String, String> compileCache, Map<String, Rule> ruleCache) {
        try {
            // compile the Java source
            compiler.compile(compileCache, ruleCache);
        } catch (CharSequenceCompilerException e) {
            logger.error("Exception:", e);
            log(e.getDiagnostics());
        }
    }

    public void shutdown() {
        executorService.shutdown();
        // shutdown wait until all tasks complete and close db in final block.
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.error("Exception:", e);
        }
    }

    /**
     * Log diagnostics into the console
     *
     * @param diagnostics
     *            iterable compiler diagnostics
     */
    private void log(final DiagnosticCollector<JavaFileObject> diagnostics) {
        final StringBuilder messages = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics
                .getDiagnostics()) {
            messages.append(diagnostic.getMessage(null)).append("\n");
        }
        logger.info("Compiler: " + messages.toString());
    }
}
