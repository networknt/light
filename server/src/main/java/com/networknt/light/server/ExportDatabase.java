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
