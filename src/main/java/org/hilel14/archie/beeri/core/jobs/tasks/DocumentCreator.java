package org.hilel14.archie.beeri.core.jobs.tasks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.solr.common.SolrInputDocument;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.model.ImportFileTicket;

/**
 *
 * @author hilel14
 */
public class DocumentCreator implements TaskProcessor {

    static final Logger LOGGER = LoggerFactory.getLogger(DocumentCreator.class);
    final Config config;
    final DateFormat iso8601TimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public DocumentCreator(Config config) {
        this.config = config;
    }

    @Override
    public void proccess(ImportFileTicket ticket) throws Exception {
        LOGGER.debug("Adding {} to Solr", ticket.getFileName());
        SolrInputDocument solrDoc = new SolrInputDocument();
        Map<String, Object> values = ticket.toDocument();
        // set id and import time
        solrDoc.addField("id", ticket.getUuid());
        solrDoc.addField("importTime", iso8601TimeFormat.format(Calendar.getInstance().getTime()));
        // add other fields
        for (String key : values.keySet()) {
            Object value = values.get(key);
            if (value != null) {
                if (value.getClass().equals(String.class)) {
                    value = value.toString().trim();
                }
                solrDoc.addField(key, value);
            }
        }
        // send the request to Solr
        config.getSolrClient().add(solrDoc);
        config.getSolrClient().commit();
    }

}
