package org.hilel14.archie.beeri.core.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.DeleteDocumentsJob;
import org.hilel14.archie.beeri.core.jobs.ImportFolderJob;
import org.hilel14.archie.beeri.core.jobs.UpdateDocumentsJob;
import org.hilel14.archie.beeri.core.jobs.model.ArchieDocument;
import org.hilel14.archie.beeri.core.jobs.model.ImportFolderForm;

/**
 *
 * @author hilel14
 */
public class JobsConsumer {

    static final Logger LOGGER = LoggerFactory.getLogger(JobsConsumer.class);
    final Config config;
    boolean stop = false;

    public static void main(String[] args) {
        try {
            Config config = new Config();
            JobsConsumer jobsConsumer = new JobsConsumer(config);
            jobsConsumer.pollJobsQueue();
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }

    public JobsConsumer(Config config) throws Exception {
        this.config = config;
    }

    public void pollJobsQueue() throws Exception {
        LOGGER.info("Connecting to jms broker {}", config.getJmsFactory().getBrokerURL());
        Connection connection = config.getJmsFactory().createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(config.getJmsQueueName());
        MessageConsumer consumer = session.createConsumer(queue);
        LOGGER.info("Consuming messages from queue {}", config.getJmsQueueName());
        connection.start();
        while (true) {
            ActiveMQTextMessage message = (ActiveMQTextMessage) consumer.receive();
            runJob(message.getStringProperty("archieJobName"), message.getText());
        }
    }

    private void runJob(String jobName, String jobSpec) throws Exception, Exception {

        switch (jobName) {
            case "import-folder":
                ImportFolderForm form = ImportFolderForm.unmarshal(jobSpec);
                ImportFolderJob importFolderJob = new ImportFolderJob(config);
                importFolderJob.run(form);
                break;
            case "update-documents":
                UpdateDocumentsJob updateDocumentsJob = new UpdateDocumentsJob(config);
                List<ArchieDocument> docs
                        = new ObjectMapper().readValue(jobSpec, new TypeReference<List<ArchieDocument>>() {
                        });
                updateDocumentsJob.run(docs);
                break;
            case "delete-documents":
                List<String> ids
                        = new ObjectMapper()
                                .readValue(jobSpec, new TypeReference<List<String>>() {
                                });
                DeleteDocumentsJob deleteDocumentsJob = new DeleteDocumentsJob(config);
                deleteDocumentsJob.run(ids);
                break;
            default:
                throw new IllegalArgumentException("Unknown job name: " + jobName);
        }
    }

}
