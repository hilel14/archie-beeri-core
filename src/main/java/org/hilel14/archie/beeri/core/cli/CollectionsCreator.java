package org.hilel14.archie.beeri.core.cli;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.tools.DcCollectionsTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class CollectionsCreator {

    static final Logger LOGGER = LoggerFactory.getLogger(CollectionsCreator.class);

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("mandatory argument: text input file with list of collections to create");
            return;
        }
        Path inFile = Paths.get(args[0]);
        LOGGER.info("Creating collections from file {}", inFile);
        try (Reader in = new FileReader(inFile.toFile())) {
            Config config = new Config();
            DcCollectionsTool tool = new DcCollectionsTool(config);
            List<String> lines = Files.readAllLines(inFile);
            for (String expression : lines) {
                tool.autoCreateCollections(expression);
            }
            LOGGER.info("{} collections created", lines.size());
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }
}
