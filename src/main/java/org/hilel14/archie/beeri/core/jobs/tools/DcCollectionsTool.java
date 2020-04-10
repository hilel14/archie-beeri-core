package org.hilel14.archie.beeri.core.jobs.tools;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.model.ArchieDocument;

public class DcCollectionsTool {

    static final Logger LOGGER = LoggerFactory.getLogger(DcCollectionsTool.class);
    public final static String SEPARATOR = " >> ";
    public final static DateFormat ISO8601_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    final Config config;

    public DcCollectionsTool(Config config) {
        this.config = config;
    }

    public List<ArchieDocument> splitCollectionsExpression(String expression) {
        List<ArchieDocument> docs = new ArrayList<>();
        String[] parts = expression.split(SEPARATOR);
        ArchieDocument doc = new ArchieDocument();
        doc.setDcTitle(parts[0]);
        docs.add(doc);
        for (int i = 1; i < parts.length; i++) {
            String parent = doc.getDcTitle();
            doc = new ArchieDocument();
            doc.setDcIsPartOf(parent);
            doc.setDcTitle(parent + SEPARATOR + parts[i]);
            docs.add(doc);
        }
        return docs;
    }

    public void autoCreateCollections(String expression) throws SolrServerException, IOException {
        LOGGER.debug("analyzing hierarchy expression {}", expression);
        if (expression != null) {
            List<ArchieDocument> docs = splitCollectionsExpression(expression);
            for (ArchieDocument doc : docs) {
                SolrQuery query = new SolrQuery();
                query.set("q", "dcType:collection");
                query.set("fq", "dcTitleString:" + "\"" + doc.getDcTitle() + "\"");
                query.setFields("id");
                QueryResponse response = config.getSolrClient().query(query);
                SolrDocumentList list = response.getResults();
                if (list.getNumFound() == 0) {
                    createCollection(doc.getDcTitle(), doc.getDcIsPartOf());
                }
            }
        }
    }

    private void createCollection(String title, String parent) throws SolrServerException, IOException {
        LOGGER.debug("Creating collection {} with parent {}", title, parent);
        SolrInputDocument solrDoc = new SolrInputDocument();
        solrDoc.addField("id", UUID.randomUUID().toString());
        solrDoc.addField("importTime", ISO8601_TIME_FORMAT.format(Calendar.getInstance().getTime()));
        solrDoc.addField("dcType", "collection");
        solrDoc.addField("dcTitle", title);
        if (parent != null) {
            parent = parent.trim();
            if (!parent.isEmpty()) {
                solrDoc.addField("dcIsPartOf", parent);
            }
        }
        config.getSolrClient().add(solrDoc);
        config.getSolrClient().commit();
    }

}
