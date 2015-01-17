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

package com.networknt.light.server.handler.loader;

import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by steve on 23/08/14.
 */
public class TemplateLoader extends Loader {
    static public String templateFolder = "template";

    public static void loadTemplateOriental() {
        File folder = getFileFromResourceFolder(templateFolder);
        if(folder != null) {
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {

                loadTemplateFile(listOfFiles[i]);

            }
        }
    }

    private static void loadTemplateFile(File file) {
        Scanner scan = null;

        try {
            scan = new Scanner(file, Loader.encoding);
            String content = scan.useDelimiter("\\Z").next();
            //System.out.println(content);
            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
            OSchema schema = db.getMetadata().getSchema();
            for (OClass oClass : schema.getClasses()) {
                System.out.println(oClass.getName());
            }
            String templateName = file.getName();
            try {
                db.begin();
                // remove the document for the class if there are any.
                OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("select from Template where id = ?");
                List<ODocument> result = db.command(query).execute(templateName);
                for (ODocument template : result) {
                    template.delete();
                }
                ODocument doc = new ODocument(schema.getClass("Template"));
                doc.field("id", templateName);
                doc.field("content", content);
                doc.save();
                db.commit();
                System.out.println("Template " + templateName + " is loaded!");
            } catch (Exception e) {
                db.rollback();
                e.printStackTrace();
            } finally {
                db.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (scan != null) scan.close();
        }
    }
}
