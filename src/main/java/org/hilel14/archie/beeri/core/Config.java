package org.hilel14.archie.beeri.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.hilel14.archie.beeri.core.storage.AwsBucketConnector;
import org.hilel14.archie.beeri.core.storage.SimpleStorageConnector;
import org.hilel14.archie.beeri.core.storage.StorageConnector;

/**
 *
 * @author hilel14
 */
public class Config {

    public static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    private final String archieEnv;
    private StorageConnector storageConnector;
    private Path workFolder;
    private final List<String> validFileFormats;
    private final List<String> validOcrFormats;
    private final String convertCommand;
    private final String tesseractCommand;
    private final String convertImageCommand;
    private final String convertPdfCommand;
    private final String jmsQueueName;
    private final String googleClientId;
    private BasicDataSource dataSource;
    private ActiveMQConnectionFactory jmsFactory;
    private final SolrClient solrClient;
    private final Set<String> repositories = new HashSet<>();

    public Config() throws Exception {
        Properties p = loadProperties();
        // general properties
        archieEnv = p.getProperty("archie.environment");
        LOGGER.info("archieEnv = {}", archieEnv);
        validFileFormats = Arrays.asList(p.getProperty("valid.file.formats").split(","));
        // jobs and tasks properties
        convertCommand = p.getProperty("imagemagic.convert");
        tesseractCommand = p.getProperty("tesseract");
        validOcrFormats = Arrays.asList(p.getProperty("valid.ocr.formats").split(","));
        convertImageCommand = p.getProperty("convert.image.preview");
        convertPdfCommand = p.getProperty("convert.pdf.preview");
        // jms properties
        jmsQueueName = p.getProperty("archie.jms.queue");
        // angularx-social-login properties
        googleClientId = p.getProperty("google.client.id");
        createJdbcDataSource(p);
        createJmsConnectionFactory(p);
        solrClient = new HttpSolrClient.Builder(p.getProperty("solr.base")).build();
        //jmsBrokerUrl = p.getProperty("archie.jms.broker");
        //jmsQueueName = p.getProperty("archie.jms.queue");
        createStorageConnector(p);
        repositories.add("public");
        repositories.add("private");
        repositories.add("secret");
    }

    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        // First priority: get location of properties file from the environment
        String archieHome = System.getProperty("archieHome");
        LOGGER.info("archieHome = {}", archieHome);
        if (archieHome == null) {
            LOGGER.warn("System property archieHome not found");
        } else {
            Path propertiesFile = Paths.get(archieHome)
                    .resolve("resources")
                    .resolve("archie.beeri.properties");
            if (Files.exists(propertiesFile)) {
                LOGGER.info("Loading properties from file {}", propertiesFile);
                try (FileInputStream inputStream = new FileInputStream(propertiesFile.toString())) {
                    properties.load(inputStream);
                }
                return properties;
            } else {
                LOGGER.warn("Properties file not found: {}", propertiesFile);
            }
        }
        // Second priority: get properties file from classpath
        String name = "/archie.beeri.properties";
        InputStream in = Config.class.getResourceAsStream(name);
        if (in == null) {
            throw new IOException("Classpath not found: " + name);
        } else {
            LOGGER.info("Loading properties from classpath resource {}", name);
            properties.load(in);
            return properties;
        }
    }

    private void createJmsConnectionFactory(Properties props) {
        String brokerUrl = props.getProperty("archie.jms.broker");
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        jmsFactory = factory;
    }

    private void createJdbcDataSource(Properties props) {
        dataSource = new BasicDataSource();
        getDataSource().setUrl(props.getProperty("archie.jdbc.url"));
        getDataSource().setDriverClassName(props.getProperty("archie.jdbc.driver"));
        getDataSource().setUsername(props.getProperty("archie.jdbc.user"));
        getDataSource().setPassword(props.getProperty("archie.jdbc.password"));
        getDataSource().setMinIdle(5);
        getDataSource().setMaxIdle(10);
        getDataSource().setMaxOpenPreparedStatements(100);
        LOGGER.info("Dastasource created for {}", props.getProperty("archie.jdbc.url"));
    }

    private void createStorageConnector(Properties props) throws Exception {
        workFolder = Paths.get(props.getProperty("work.folder"));
        switch (props.getProperty("storage.connector")) {
            case "simple":
                LOGGER.info("Creating simple storage connector");
                storageConnector = new SimpleStorageConnector();
                storageConnector.setup(props);
                break;
            case "aws":
                LOGGER.info("Creating AWS bucket storage connector");
                storageConnector = new AwsBucketConnector();
                storageConnector.setup(props);
                break;
            default:
                throw new IllegalArgumentException("unknown storage connector: " + props.getProperty("storage.connector"));
        }
    }

    /**
     * @return the validFileFormats
     */
    public List<String> getValidFileFormats() {
        return validFileFormats;
    }

    /**
     * @return the validOcrFormats
     */
    public List<String> getValidOcrFormats() {
        return validOcrFormats;
    }

    /**
     * @return the convertCommand
     */
    public String getConvertCommand() {
        return convertCommand;
    }

    /**
     * @return the tesseractCommand
     */
    public String getTesseractCommand() {
        return tesseractCommand;
    }

    /**
     * @return the convertImageCommand
     */
    public String getConvertImageCommand() {
        return convertImageCommand;
    }

    /**
     * @return the convertPdfCommand
     */
    public String getConvertPdfCommand() {
        return convertPdfCommand;
    }

    /**
     * @return the dataSource
     */
    public BasicDataSource getDataSource() {
        return dataSource;
    }

    /**
     * @return the jmsFactory
     */
    public ActiveMQConnectionFactory getJmsFactory() {
        return jmsFactory;
    }

    /**
     * @return the solrClient
     */
    public SolrClient getSolrClient() {
        return solrClient;
    }

    /**
     * @return the jmsQueueName
     */
    public String getJmsQueueName() {
        return jmsQueueName;
    }

    /**
     * @return the archieEnv
     */
    public String getArchieEnv() {
        return archieEnv;
    }

    /**
     * @return the storageConnector
     */
    public StorageConnector getStorageConnector() {
        return storageConnector;
    }

    /**
     * @return the repositories
     */
    public Set<String> getRepositories() {
        return repositories;
    }

    /**
     * @return the workFolder
     */
    public Path getWorkFolder() {
        return workFolder;
    }

    /**
     * @return the googleClientId
     */
    public String getGoogleClientId() {
        return googleClientId;
    }

}
