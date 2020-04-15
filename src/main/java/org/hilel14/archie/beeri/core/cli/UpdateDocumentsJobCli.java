package org.hilel14.archie.beeri.core.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.solr.client.solrj.SolrServerException;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.UpdateDocumentsJob;
import org.hilel14.archie.beeri.core.jobs.model.ArchieDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 *
 * Update Solr index based on CSV file
 */
public class UpdateDocumentsJobCli {

    static final Logger LOGGER = LoggerFactory.getLogger(UpdateDocumentsJobCli.class);

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        try {
            CommandLine cmd = parser.parse(options, args);
            String input = cmd.getOptionValue("input").trim();
            Config config = new Config();
            UpdateDocumentsJob job = new UpdateDocumentsJob(config);
            Path inFile = Paths.get(input);
            switch (input) {
                case "json":
                    processJson(job, inFile);
                    break;
                case "csv":
                    processCsv(job, inFile);
                    break;
                default:
                    System.out.println("Invalid input: " + input);
            }
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("update-documents", options);
            System.exit(1);
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }

    static void processCsv(UpdateDocumentsJob job, Path inFile)
            throws IOException, SolrServerException {
        LOGGER.info("Updating Solr index from csv file {}", inFile);
        try (Reader in = new FileReader(inFile.toFile())) {
            job.runCsv(in);
        }
    }

    static void processJson(UpdateDocumentsJob job, Path inFile)
            throws IOException, SolrServerException {
        LOGGER.info("Updating Solr index from json file {}", inFile);
        String attributes = new String(Files.readAllBytes(inFile));
        List<ArchieDocument> docs
                = new ObjectMapper().readValue(attributes, new TypeReference<List<ArchieDocument>>() {
                });
        job.run(docs);
    }

    static Options createOptions() {
        Options options = new Options();
        Option option;
        // input
        option = new Option("i", "Input. One of: json, csv.");
        option.setLongOpt("input");
        option.setArgs(1);
        option.setRequired(true);
        options.addOption(option);
        // return
        return options;
    }
}
