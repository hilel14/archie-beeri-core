package org.hilel14.archie.beeri.core.jobs.tasks;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.commons.codec.digest.DigestUtils;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.model.ImportFileTicket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class DigestCalculator implements TaskProcessor {

    static final Logger LOGGER = LoggerFactory.getLogger(DigestCalculator.class);
    final Config config;

    public DigestCalculator(Config config) {
        this.config = config;
    }

    @Override
    public void proccess(ImportFileTicket ticket, Path path) throws Exception {
        LOGGER.debug("Calculating digest for file {}", ticket.getFileName());
        try (InputStream in = new FileInputStream(path.toFile())) {
            String digest = DigestUtils.md5Hex(in);
            ticket.setFileDigest(digest);
        }
    }

}
