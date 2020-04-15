package org.hilel14.archie.beeri.core.cli;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.solr.client.solrj.SolrServerException;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.UpdateDocumentsJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 *
 * Update Solr index based on CSV file
 */
public class BatchUpdate {

    static final Logger LOGGER = LoggerFactory.getLogger(BatchUpdate.class);

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("mandatory argument: csv input file");
            return;
        }
        Path inFile = Paths.get(args[0]);
        LOGGER.info("Updating Solr index from file {}", inFile);
        try (Reader in = new FileReader(inFile.toFile())) {
            Config config = new Config();
            UpdateDocumentsJob job = new UpdateDocumentsJob(config);
            job.runCsv(in);
        } catch (IOException | SolrServerException ex) {
            LOGGER.error(null, ex);
        }
    }
}
