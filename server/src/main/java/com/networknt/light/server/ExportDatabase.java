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

import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.tool.ODatabaseExport;

import java.io.IOException;

/**
 * Created by husteve on 10/7/2014.
 */
public class ExportDatabase {
    public static void main(final String[] args) {
        exp();
    }

    public static void exp() {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try{
            OCommandOutputListener listener = new OCommandOutputListener() {
                @Override
                public void onMessage(String iText) {
                    System.out.print(iText);
                }
            };

            ODatabaseExport export = new ODatabaseExport(db, "/tmp/export", listener);
            export.exportDatabase();
            export.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            db.close();
        }
    }
}
