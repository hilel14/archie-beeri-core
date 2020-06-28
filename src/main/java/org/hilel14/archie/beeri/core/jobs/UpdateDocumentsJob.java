package org.hilel14.archie.beeri.core.jobs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.Reader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.model.ArchieDocument;

/**
 *
 * @author hilel14
 */
public class UpdateDocumentsJob {

    static final Logger LOGGER = LoggerFactory.getLogger(UpdateDocumentsJob.class);
    final Config config;
    final DateFormat iso8601TimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    final String[] previewFileNameExtentions = new String[]{"png", "txt"};

    public UpdateDocumentsJob(Config config) {
        this.config = config;
    }

    public void run(List<ArchieDocument> docs) throws Exception {
        LOGGER.debug("Updating {} documents", docs.size());
        for (ArchieDocument archdoc : docs) {
            update(archdoc, config.getSolrClient());
        }
        config.getSolrClient().commit();
        LOGGER.debug("Update documents job completed successfully");
    }

    public void runCsv(Reader in) throws Exception {
        LOGGER.info("Updating Solr index from CSV file...");
        int count = 0;
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            count++;
            ArchieDocument doc = new ArchieDocument(record.toMap());
            update(doc, config.getSolrClient());
        }
        LOGGER.info("Commiting changes to {} documents", count);
        config.getSolrClient().commit();
        LOGGER.info("The operation completed successfully");
    }

    public void update(ArchieDocument archdoc, SolrClient solrClient)
            throws Exception {
        // Create Solr Document
        SolrInputDocument soldoc = new SolrInputDocument();
        // Set the id field
        soldoc.addField("id", archdoc.getId());
        // Set Ohter fields
        setField("dcTitle", archdoc.getDcTitle(), soldoc);
        setField("dcDate", archdoc.getDcDate(), soldoc);
        setField("dcCreator", archdoc.getDcCreator(), soldoc);
        setField("dcDescription", archdoc.getDcDescription(), soldoc);
        setField("dcType", archdoc.getDcType(), soldoc);
        setField("dcFormat", archdoc.getDcFormat(), soldoc);
        setField("dcSubject", archdoc.getDcSubject(), soldoc);
        setField("storageLocation", archdoc.getStorageLocation(), soldoc);
        setField("dcIsPartOf", archdoc.getDcIsPartOf(), soldoc);
        setField("sortCode", archdoc.getSortCode(), soldoc);
        // set access rights
        setField("dcAccessRights", archdoc.getDcAccessRights(), soldoc);
        if (archdoc.getDcFormat() != null) {
            moveFiles(archdoc);
        }
        // add to solr
        solrClient.add(soldoc);
    }

    private void setField(String name, Object value, SolrInputDocument soldoc) {
        if (value != null) {
            if (value.getClass().equals(String.class)) {
                value = value.toString().trim();
            }
            Map<String, Object> update = new HashMap<>();
            update.put("set", value);
            soldoc.addField(name, update);
        }
    }

    private void moveFiles(ArchieDocument doc) throws Exception {
        String sourceRepository = findSourceRepository("originals", doc.originalFileName());
        if (sourceRepository.isEmpty()) {
            return;
        }
        String targetRepository = doc.getDcAccessRights();
        if (sourceRepository.equalsIgnoreCase(targetRepository)) {
            return;
        }
        LOGGER.debug("Moving originals/{} from {} to {}", doc.originalFileName(), sourceRepository, targetRepository);
        config.getStorageConnector().move(sourceRepository, targetRepository, "originals", doc.originalFileName());
        // thumbnails
        if (config.getStorageConnector().exist(sourceRepository, "thumbnails", doc.thumbnailFileName())) {
            config.getStorageConnector().move(sourceRepository, targetRepository, "thumbnails", doc.thumbnailFileName());
        }
        // text
        if (config.getStorageConnector().exist(sourceRepository, "text", doc.textFileName())) {
            config.getStorageConnector().move(sourceRepository, targetRepository, "text", doc.textFileName());
        }
    }

    private String findSourceRepository(String container, String file) {
        for (String repository : config.getRepositories()) {
            if (config.getStorageConnector().exist(repository, container, file)) {
                return repository;
            }
        }
        return "";
    }
}
