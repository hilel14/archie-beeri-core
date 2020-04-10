package org.hilel14.archie.beeri.core.jobs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.Reader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.Config.AccessRights;
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
    final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        Path in = Paths.get(args[0]);
        try {
            Config config = new Config();
            String attributes = new String(Files.readAllBytes(in));
            UpdateDocumentsJob job = new UpdateDocumentsJob(config);
            job.run(attributes);
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }

    public UpdateDocumentsJob(Config config) {
        this.config = config;
    }

    public void run(String attributes) throws Exception {
        List<ArchieDocument> docs
                = mapper.readValue(attributes, new TypeReference<List<ArchieDocument>>() {
                });
        runDirectly(docs);
    }

    public void runDirectly(List<ArchieDocument> docs) throws SolrServerException, IOException {
        LOGGER.debug("Updating {} documents", docs.size());
        for (ArchieDocument archdoc : docs) {
            update(archdoc, config.getSolrClient());
        }
        config.getSolrClient().commit();
        LOGGER.debug("Update documents job completed successfully");
    }

    public void runCsv(Reader in) throws IOException, SolrServerException {
        LOGGER.info("Updating Solr index from CSV file...");
        int count = 0;
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            count++;
            ArchieDocument doc = mapper.convertValue(record.toMap(), ArchieDocument.class);
            update(doc, config.getSolrClient());
        }
        LOGGER.info("Commiting changes to {} documents", count);
        config.getSolrClient().commit();
        LOGGER.info("The operation completed successfully");
    }

    public void update(ArchieDocument archdoc, SolrClient solrClient)
            throws SolrServerException, IOException {
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
        // Create dc collection hierarchy if not exists
        createDcCollection(archdoc.getDcIsPartOf(), solrClient);
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

    private void moveFiles(ArchieDocument doc) throws IOException {
        Path source = findSourceFile(doc);
        AccessRights targetAccess = AccessRights.valueOf(doc.getDcAccessRights().toUpperCase());
        Path target
                = config
                        .getAssetFolder(targetAccess, Config.AssetContainer.ASSETS)
                        .resolve(doc.getId() + "." + doc.getDcFormat());
        LOGGER.debug("Moving {} to {}", source, target);
        Files.move(source, target);
        movePreviewFiles(source, target, doc.getId());
    }

    private void movePreviewFiles(Path assetSource, Path assetTarget, String id) throws IOException {
        String sourceAccess = assetSource.getParent().getParent().getFileName().toString().toUpperCase();
        String targetAccess = assetTarget.getParent().getParent().getFileName().toString().toUpperCase();
        Path sourceFolder = config
                .getAssetFolder(AccessRights.valueOf(sourceAccess), Config.AssetContainer.PREVIEW);
        Path targetFolder = config
                .getAssetFolder(AccessRights.valueOf(targetAccess), Config.AssetContainer.PREVIEW);
        LOGGER.debug("Moving files from {} to {}", sourceFolder, targetFolder);
        Path source;
        Path target;
        for (String ext : previewFileNameExtentions) {
            source = sourceFolder.resolve(id + "." + ext);
            LOGGER.debug("Checking if source path {} exists", source);
            if (Files.exists(source)) {
                target = targetFolder.resolve(id + "." + ext);
                Files.move(source, target);
            }
        }
    }

    private Path findSourceFile(ArchieDocument doc) throws FileNotFoundException {
        Path source;
        for (AccessRights accessRights : AccessRights.values()) {
            source = config.getAssetFolder(accessRights, Config.AssetContainer.ASSETS)
                    .resolve(doc.getId() + "." + doc.getDcFormat());
            if (Files.exists(source)) {
                return source;
            }
        }
        throw new FileNotFoundException("File " + doc.getId() + "." + doc.getDcFormat() + " not found in asset store.");
    }

    private void createDcCollection(String title, SolrClient solrClient) throws SolrServerException, IOException {
        if (title != null) {
            SolrQuery query = new SolrQuery();
            query.set("q", "dcType:collection");
            query.set("fq", "dcTitle:" + "\"" + title + "\"");
            query.setFields("id");
            QueryResponse response = solrClient.query(query);
            SolrDocumentList list = response.getResults();
            if (list.getNumFound() == 0) {
                LOGGER.debug("Creating dc-collection document {}", title);
                SolrInputDocument solrDoc = new SolrInputDocument();
                solrDoc.addField("id", UUID.randomUUID().toString());
                solrDoc.addField("importTime", iso8601TimeFormat.format(Calendar.getInstance().getTime()));
                solrDoc.addField("dcType", "collection");
                solrDoc.addField("dcTitle", title);
                solrClient.add(solrDoc);
                solrClient.commit();
            }
        }
    }

}
