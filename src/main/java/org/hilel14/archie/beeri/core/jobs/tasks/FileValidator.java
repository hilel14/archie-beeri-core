package org.hilel14.archie.beeri.core.jobs.tasks;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.model.ImportFileTicket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class FileValidator implements TaskProcessor {

    static final Logger LOGGER = LoggerFactory.getLogger(FileValidator.class);
    final Pattern startsWithWordCharacter = Pattern.compile("[a-zA-Z_0-9_א-ת].+");
    final Config config;

    public FileValidator(Config config) {
        this.config = config;
    }

    @Override
    public void proccess(ImportFileTicket ticket) {
        LOGGER.debug("Validating file {}", ticket.getFileName());
        Path path = config.getImportFolder()
                .resolve(ticket.getImportFolderForm().getFolderName())
                .resolve(ticket.getFileName());
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            ticket.setImportStatusCode(ImportFileTicket.INVALID_FILE);
            ticket.setImportStatusText("File not found");
            return;
        }
        if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
            ticket.setImportStatusCode(ImportFileTicket.INVALID_FILE);
            ticket.setImportStatusText("Not a regular file");
            return;
        }
        if (!startsWithWordCharacter.matcher(ticket.getFileName()).matches()) {
            ticket.setImportStatusCode(ImportFileTicket.INVALID_FILE);
            ticket.setImportStatusText("File name not starting with word character");
            return;
        }
        String extension = FilenameUtils.getExtension(ticket.getFileName());
        if (extension.trim().isEmpty()) {
            ticket.setImportStatusCode(ImportFileTicket.INVALID_FILE);
            ticket.setImportStatusText("File name without extention");
            return;
        }
        if (!config.getValidFileFormats().contains(extension.toLowerCase())) {
            ticket.setImportStatusCode(ImportFileTicket.INVALID_FILE);
            ticket.setImportStatusText("Unknown file name extention: " + extension);
        }
    }
}
