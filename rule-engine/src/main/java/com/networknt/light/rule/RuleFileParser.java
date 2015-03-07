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

import org.slf4j.LoggerFactory;
import sun.misc.Launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by steve on 31/07/14.
 */
public class RuleFileParser {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(RuleFileParser.class);

    final static Charset ENCODING = StandardCharsets.UTF_8;

    public static void main(String... aArgs) throws IOException {
        RuleFileParser parser = new RuleFileParser();
        parser.readTextFile("/home/steve/light/rule/src/main/resources/rule/com/networknt/light/rule/DataFeedRule.rule");

    }

    void readTextFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        try (Scanner scanner =  new Scanner(path, ENCODING.name())){
            while (scanner.hasNextLine()){
                //process each line in some way
                log(scanner.nextLine());
            }
        }
    }

    /**
     Constructor.
    */
    public RuleFileParser() {
    }

    private static void log(Object aObject){
        logger.debug(String.valueOf(aObject));
    }

    static void loadRule() {
        final String path = "rule";
        final File jarFile = new File(Rule.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        if(jarFile.isFile()) {  // Run with JAR file
            try {
                final JarFile jar = new JarFile(jarFile);
                final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                while(entries.hasMoreElements()) {
                    final String name = entries.nextElement().getName();
                    if (name.startsWith(path + "/")) { //filter according to the path
                        logger.debug(name);
                    }
                }
                jar.close();
            } catch (Exception e) {
                logger.error("Exception:", e);
            }
        } else { // Run with IDE
            final URL url = Launcher.class.getResource("/" + path);
            if (url != null) {
                try {
                    final File apps = new File(url.toURI());
                    for (File app : apps.listFiles()) {
                        logger.debug(app.getAbsolutePath());
                    }
                } catch (URISyntaxException ex) {
                    // never happens
                }
            }
        }
    }

    /**
     * Return the rule Java source, substituting the given package name,
     * imports, rule name, and body of the rule
     *
     * @throws IOException
     */
    private String fillTemplate(String packageName, String importClass,
                                String ruleName, String code) throws IOException {
        String ruleTemplate = readRuleTemplate();
        String source = null;
        /*
		if(code.startsWith("{")) {
			source = functionTemplate.replace("$packageName", packageName)
					.replace("$importClass", importClass == null? "" : importClass)
					.replace("$className", className)
					.replace("$function", code);
		} else {
			source = expressionTemplate.replace("$packageName", packageName)
					.replace("$importClass", importClass == null? "" : importClass)
					.replace("$className", className)
					.replace("$expression", code);
		}
		*/
        return source;
    }

    /**
     * Read the rule source template
     *
     * @return a source template
     * @throws IOException
     */
    private String readRuleTemplate() throws IOException {
        InputStream is = Rule.class
                .getResourceAsStream("Rule.java.template");
        int size = is.available();
        byte bytes[] = new byte[size];
        if (size != is.read(bytes, 0, size))
            throw new IOException();
        return new String(bytes, "UTF-8");
    }

}