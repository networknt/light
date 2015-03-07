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
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by husteve on 8/1/14.
 */
public class RuleEngine {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RuleEngine.class);

    // build a ruleCache with 1000 rules as max. This cache is thread safe.
    private ConcurrentLinkedHashMap<String, Rule> ruleCache = new ConcurrentLinkedHashMap.Builder<String, Rule>().maximumWeightedCapacity(1000).build();
    // ruleCache expiration time and set it to next day's midnight if it passes its current time value
    private static long cacheExpirationTime = 0L;

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
     * Once impRule command is executed, this will be called to remove the rule instance from cache.
     * The next time the same rule is called, it will be loaded from database which is a newer version.
     *
     * Note: in normal case, impRule command only be called on dev environment as it has to go through
     * the web interface. By simply replay impRule Event as deployment on production, this method won't
     * be called and the rule will be refreshed the next day morning. (TODO this has been commented out
     * and you have to restart the server as the class loader won't try to find the latest compile class
     * but simply return the old version of the class. Also, you might have issue if new class replacing
     * old class during runtime. It's better to create a new rule and use A/B testing feature)
     *
     * @param ruleClass Rule class name
     * @return Rule
     */
    public Rule removeRule(String ruleClass) {
        return ruleCache.remove(ruleClass);
    }

    /**
     * It is not a good idea to reload rules everyday if they are not changed. Remove the logic here.
     * @param ruleClass
     * @return
     */
    Rule loadRule(String ruleClass) {
        /*
        if(System.currentTimeMillis() > cacheExpirationTime) {
            // ruleCache will be cleared every midnight and the cache will be filled once each rule is called.
            ruleCache.clear();
            cacheExpirationTime = getNextMidNightTime();
        }
        */
        Rule rule = ruleCache.get(ruleClass);
        if (rule == null) {
            try {
                rule = loadRuleFromDb(ruleClass);
            } catch (Exception e) {
                logger.error("Exception loadRuleFromDb " + ruleClass, e);
            }
            if(rule != null) {
                ruleCache.put(ruleClass, rule);
            } else {
                // could not find the rule in db
                logger.error("Could not find rule in Db " + ruleClass);
            }
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
            throw new Exception("Invalid rule class name " + ruleClass);
        }
        Rule rule = loadRule(ruleClass);
        return rule.execute(objects);
    }

    /**
     * Compile rule Java source for a Rule Object
     * @param ruleClass full package and class name
     * @param sourceCode java source code of the rule.
     * @return an rule object that can be called to execute rule
     */
    Rule compileRule(String ruleClass, String sourceCode) {
        logger.debug("Compile ruleClass = " + ruleClass);
        Rule rule = null;
        try {
            // compile the Java source
            final DiagnosticCollector<JavaFileObject> errs = new DiagnosticCollector<JavaFileObject>();
            Class<Rule> compiledRule = compiler.compile(ruleClass, sourceCode, errs, new Class<?>[] { Rule.class });
            log(errs);
            rule = compiledRule.newInstance();
        } catch (CharSequenceCompilerException e) {
            logger.error("Exception:", e);
            log(e.getDiagnostics());
        } catch (InstantiationException e) {
            logger.error("Exception:", e);
        } catch (IllegalAccessException e) {
            logger.error("Exception:", e);
        }
        return rule;
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
     * Load rule from database
     *
     */
    private Rule loadRuleFromDb(String ruleClass) {
        Rule rule = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Vertex v = graph.getVertexByKey("Rule.ruleClass", ruleClass);
            if(v != null) {
                String sourceCode = v.getProperty("sourceCode");
                rule = compileRule(ruleClass, sourceCode);
            }
        } catch (Exception e) {
            logger.error("Exception while loading from db " + ruleClass, e);
        } finally {
            graph.shutdown();
        }
        return rule;
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

    /**
     * based on current system date to get next day's midnight time
     * @return next mid night time
     */
    /*
    private static long getNextMidNightTime() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // next day
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTimeInMillis();
    }
    */
}
