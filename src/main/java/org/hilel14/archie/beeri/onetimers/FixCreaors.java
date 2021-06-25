package org.hilel14.archie.beeri.onetimers;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.UpdateDocumentsJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class FixCreaors {

    static final Logger LOGGER = LoggerFactory.getLogger(FixCreaors.class);
    Config config;
    UpdateDocumentsJob updateJob;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Mandatory arguments: input-file");
            System.out.println("");
            System.exit(1);
        }
        Path inPath = Paths.get(args[0]);
        try {
            FixCreaors app = new FixCreaors();
            app.processInputFile(inPath);
        } catch (Exception ex) {
            LOGGER.error("Error while fixing creators", ex);
        }
    }

    public FixCreaors() throws Exception {
        config = new Config();
        updateJob = new UpdateDocumentsJob(config);
    }

    private void processInputFile(Path inPath) throws Exception {
        LOGGER.info("Processing input file {}", inPath);
        try (FileReader reader = new FileReader(inPath.toFile(), Charset.forName("utf-8"));) {
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                // Map<String,String> m = record.toMap();
                String existingCreator = record.get(0);
                String newCreator = record.get(1);
                String deleteCreator = record.get(2);
                String copyToDescription = record.get(3);
                if (newCreator.isBlank() && deleteCreator.isBlank() && copyToDescription.isBlank()) {
                    LOGGER.info("All fields are blank for creator {}", existingCreator);
                } else {
                    SolrDocumentList list = findDocuments(existingCreator);
                    LOGGER.info("{} documents found for {} ", list.getNumFound(), existingCreator);
                    if (list.getNumFound() > 0) {
                        processCreator(list, existingCreator, newCreator, deleteCreator, copyToDescription);
                    }
                }
            }
        }
    }

    private SolrDocumentList findDocuments(String existingCreator) throws SolrServerException, IOException {
        LOGGER.debug("Finding documents for creator {}", existingCreator);
        // String q = "dcCreator:" + "\"" + prepareQueryString(existingCreator) + "\"";
        String q = "dcCreator:" + prepareQueryString(existingCreator);
        LOGGER.debug("{}", q);
        SolrQuery query = new SolrQuery(q);
        query.addField("id");
        query.addField("dcCreator");
        query.addField("dcDescription");
        query.setStart(0);
        query.setRows(Integer.MAX_VALUE);
        QueryResponse response = config.getSolrClient().query(query);
        SolrDocumentList list = response.getResults();
        return list;
    }

    private String prepareQueryString(String text) {
        String quotes = new String(Character.toChars(34)); // -------> "
        // String colon = new String(Character.toChars(58)); // -----> :
        String backslash = new String(Character.toChars(92)); // ----> \
        // escape special characters
        text = text.replaceAll(quotes, backslash + backslash + quotes);
        // wrap with quotes
        text = quotes + text + quotes;
        // reuturn
        return text;
    }

    private void processCreator(SolrDocumentList list, String existingCreator, String newCreator, String deleteCreator,
            String copyToDescription) throws Exception {
        for (int i = 0; i < list.getNumFound(); i++) {
            SolrDocument doc = list.get(i);
            List<Object> creators = (List<Object>) doc.get("dcCreator");
            LOGGER.info("creators = {}", creators);
            if (creators.contains(existingCreator)) {
                Map<String, Object> map = new HashMap<>();
                //map.put("id", doc.get("id"));
                if (deleteCreator.equals("כן")) {
                    creators.remove(existingCreator);
                }
                if (!copyToDescription.isBlank()) {
                    map.put("dcDescription", existingCreator);
                }
                if (!newCreator.isBlank()) {
                    creators.remove(existingCreator);
                    String[] newCreators = newCreator.split(",");
                    for (String s : newCreators) {
                        creators.add(s.trim());
                    }
                }
                if (creators.isEmpty()) {
                    map.put("dcCreator", null);
                } else {
                    map.put("dcCreator", creators);
                }
                LOGGER.info("number of creators={} in {}", creators.size(), map);
                updateJob.updateSingle(doc.get("id").toString(), map);
            } else {
                LOGGER.warn("creator {} not found in {}", existingCreator, doc.get("dcCreator"));
            }
        }
    }
}