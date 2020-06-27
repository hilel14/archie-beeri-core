package org.hilel14.archie.beeri.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class About {

    static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(About.class);

    public static void main(String[] args) {
        try {
            getMavenProperties();
            Config config = new Config();
        } catch (Exception ex) {
            LOGGER.error("Config error", ex);
        }
    }

    private static void getMavenProperties() throws IOException {
        Properties props = new Properties();
        String resourceName = "META-INF/maven/org.hilel14/archie-beeri-core/pom.properties";
        InputStream inStream = About.class.getClassLoader().getResourceAsStream(resourceName);
        if (inStream == null) {
            LOGGER.warn("Unable to load {}", resourceName);
        } else {
            props.load(inStream);
            LOGGER.info("groupId = {}", props.getProperty("groupId"));
            LOGGER.info("artifactId = {}", props.getProperty("artifactId"));
            LOGGER.info("version = {}", props.getProperty("version"));
        }
    }

}
