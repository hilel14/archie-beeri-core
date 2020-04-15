/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hilel14.archie.beeri.core.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.hilel14.archie.beeri.core.migration.SchemaMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class Migration {

    static final Logger LOGGER = LoggerFactory.getLogger(Migration.class);

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Mandatory arguments: source-collection target-collection");
            System.exit(1);
        }
        SchemaMigration app = new SchemaMigration();
        try {
            app.copyDocuments(args[0], args[1]);
            Set<String> collections = app.getCollections();
            List<String> list = new ArrayList<>(collections);
            Collections.sort(list);
            Path outFile = Paths.get(System.getProperty("java.io.tmpdir")).resolve("collections.txt");
            Files.write(outFile, list);
            LOGGER.info("The operation completed successfully. {} general collections found. Report saved in {}", collections.size(), outFile);
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }
}
