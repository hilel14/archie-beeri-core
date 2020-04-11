package org.hilel14.archie.beeri.core.jobs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.Config.AccessRights;
import org.hilel14.archie.beeri.core.jobs.model.ArchieDocument;

/**
 *
 * @author hilel14
 */
public class DeleteDocumentsJob {

    static final Logger LOGGER = LoggerFactory.getLogger(DeleteDocumentsJob.class);
    final Config config;

    public static void main(String[] args) {
        Path in = Paths.get(args[0]);
        try {
            Config config = new Config();
            String jobSpec = new String(Files.readAllBytes(in));
            List<String> ids
                    = new ObjectMapper()
                            .readValue(jobSpec, new TypeReference<List<String>>() {
                            });
            DeleteDocumentsJob job = new DeleteDocumentsJob(config);
            job.run(ids);
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }

    public DeleteDocumentsJob(Config config) {
        this.config = config;
    }

    public void run(List<String> ids) throws IOException, SolrServerException {
        LOGGER.debug("Deleting documents {}", ids);
        List<ArchieDocument> files = deleteDocuments(ids);
        LOGGER.debug("Deleting {} files", files.size());
        for (ArchieDocument doc : files) {
            deleteFiles(doc);
        }
        LOGGER.debug("Delete job completed successfully");
    }

    private List<ArchieDocument> deleteDocuments(List<String> ids) throws IOException, SolrServerException {
        String q = buildQuery(ids);
        List<ArchieDocument> files = new ArrayList<>();
        SolrQuery query = new SolrQuery();
        query.set("q", q);
        query.setFields("id", "dcFormat", "dcAccessRights");
        QueryResponse response = config.getSolrClient().query(query);
        SolrDocumentList list = response.getResults();
        LOGGER.debug("q = {} numFound = {}", q, list.getNumFound());
        for (int i = 0; i < list.getNumFound(); i++) {
            LOGGER.debug("soldoc = {}", list.get(i).toString());
            String id = list.get(i).getFieldValue("id").toString();
            Object format = list.get(i).getFieldValue("dcFormat");
            Object dcAccessRights = list.get(i).getFieldValue("dcAccessRights");
            if (format != null) {
                ArchieDocument archdoc = new ArchieDocument();
                archdoc.setId(id);
                archdoc.setDcFormat(format.toString());
                archdoc.setDcAccessRights(dcAccessRights.toString());
                files.add(archdoc);
            }
            UpdateResponse updateResponse = config.getSolrClient().deleteById(id);
            LOGGER.debug("Delete UpdateResponse: {}", updateResponse.toString());
        }
        UpdateResponse updateResponse = config.getSolrClient().commit();
        LOGGER.debug("Commit UpdateResponse: {}", updateResponse.toString());
        return files;
    }

    private String buildQuery(List<String> ids) {
        StringBuilder b = new StringBuilder();
        b.append("id:").append(ids.get(0));
        for (int i = 1; i < ids.size(); i++) {
            b.append(" OR id:").append(ids.get(i));
        }
        return b.toString();
    }

    private void deleteFiles(ArchieDocument doc) throws IOException {
        AccessRights access = Config.AccessRights.valueOf(doc.getDcAccessRights().toUpperCase());
        Path path
                = config
                        .getAssetFolder(access, Config.AssetContainer.ASSETS)
                        .resolve(doc.getId() + "." + doc.getDcFormat());
        Files.deleteIfExists(path);
        path
                = config
                        .getAssetFolder(access, Config.AssetContainer.PREVIEW)
                        .resolve(doc.getId() + ".png");
        Files.deleteIfExists(path);
        path
                = config
                        .getAssetFolder(access, Config.AssetContainer.PREVIEW)
                        .resolve(doc.getId() + ".txt");
        Files.deleteIfExists(path);
    }

}
