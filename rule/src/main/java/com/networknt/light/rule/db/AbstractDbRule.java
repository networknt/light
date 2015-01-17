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

package com.networknt.light.rule.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.networknt.light.util.Util;
import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.command.script.OCommandScript;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.tool.ODatabaseExport;
import com.orientechnologies.orient.core.db.tool.ODatabaseImport;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by steve on 10/12/14.
 */
public abstract class AbstractDbRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractDbRule.class);

    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    public abstract boolean execute (Object ...objects) throws Exception;

    protected void impDb(Map<String, Object> data) {
        String content = (String) data.get("content");
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try{
            OCommandOutputListener listener = new OCommandOutputListener() {
                @Override
                public void onMessage(String iText) {
                    logger.info(iText);
                }
            };
            InputStream is = new ByteArrayInputStream(content.getBytes());
            ODatabaseImport imp = new ODatabaseImport(db, is, listener);
            imp.importDatabase();
            imp.close();
        } catch (IOException ioe) {
            logger.error("Exception:", ioe);
        } finally {
            db.close();
        }
    }

    protected String exportEvent(String path) {
        final String[] result = new String[1];
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try{
            OCommandOutputListener listener = new OCommandOutputListener() {
                @Override
                public void onMessage(String iText) {
                    result[0] = result[0] + iText;
                }
            };
            ODatabaseExport export = new ODatabaseExport(db, path, listener);
            export.exportDatabase();
            export.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            db.close();
        }
        return result[0];
    }

    protected String execCommand(Map<String, Object> data, boolean commit) {
        String result = "";
        String script = (String) data.get("script");
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try{
            db.command(new OCommandScript("sql", script)).execute();
            if(commit) {
                db.commit();
            } else {
                db.rollback();
            }
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            result = Util.stacktraceToString(e);
        } finally {
            db.close();
        }
        return result;
    }

}
