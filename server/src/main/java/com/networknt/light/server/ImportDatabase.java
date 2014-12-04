package com.networknt.light.server;

import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.tool.ODatabaseImport;

import java.io.IOException;

/**
 * Created by husteve on 10/8/2014.
 */
public class ImportDatabase {

    public static void main(final String[] args) {
        imp();
    }

    public static void imp() {

        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();

        try{
            OCommandOutputListener listener = new OCommandOutputListener() {
                @Override
                public void onMessage(String iText) {
                    System.out.print(iText);
                }
            };

            ODatabaseImport imp = new ODatabaseImport(db, "/temp/export/export.json.gz", listener);
            imp.importDatabase();
            imp.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            db.close();
        }

    }

}
