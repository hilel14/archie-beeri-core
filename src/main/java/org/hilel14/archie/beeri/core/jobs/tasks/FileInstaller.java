package org.hilel14.archie.beeri.core.jobs.tasks;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.model.ImportFileTicket;

/**
 *
 * @author hilel14
 */
public class FileInstaller implements TaskProcessor {

    static final Logger LOGGER = LoggerFactory.getLogger(FileInstaller.class);
    final Config config;

    public FileInstaller(Config config) {
        this.config = config;
    }

    @Override
    public void proccess(ImportFileTicket ticket, Path original) throws Exception {
        String repository = ticket.getImportFolderForm().getDcAccessRights();
        // original
        config.getStorageConnector().upload(original, repository, "originals");
        // thumbnail
        Path thumbnail = generateThumbnail(ticket, original);
        if (thumbnail != null) {
            config.getStorageConnector().upload(thumbnail, repository, "thumbnails");
        }
        // text
        Path text = config.getWorkFolder().resolve("import").resolve(ticket.getUuid() + ".txt");
        if (Files.exists(text)) {
            config.getStorageConnector().upload(text, repository, "text");
        }
        // cleanup
        config.getStorageConnector().delete(
                "import",
                ticket.getImportFolderForm().getFolderName(),
                ticket.getFileName()
        );

        Files.deleteIfExists(original);
        Files.deleteIfExists(thumbnail);
        Files.deleteIfExists(text);
    }

    private Path generateThumbnail(ImportFileTicket ticket, Path original) throws Exception {
        LOGGER.debug("Generating preview for file {}", ticket.getFileName());
        switch (ticket.getFormat()) {
            case "pdf":
                return convertPdf(ticket, original);
            case "jpg":
            case "jpeg":
            case "gif":
            case "tif":
            case "tiff":
            case "png":
                return convertImage(ticket, original);
            default:
                LOGGER.debug("Unable to create preview for {} files", ticket.getFormat());
                return null;
        }
    }

    private Path convertPdf(ImportFileTicket ticket, Path original) throws Exception {
        String source = original.toString() + "[0]";
        String target = original.getParent().resolve(ticket.getUuid() + ".png").toString();
        LOGGER.debug("Executing command {} {} {}",
                config.getConvertPdfCommand(), source, target);
        CommandLine commandLine
                = CommandLine.parse(config.getConvertPdfCommand() + " " + source + " " + target);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        executor.execute(commandLine);
        return Paths.get(target);
    }

    private Path convertImage(ImportFileTicket ticket, Path original) throws Exception {
        String target = original.getParent().resolve(ticket.getUuid() + ".png").toString();
        LOGGER.debug("Executing command {} {} {}",
                config.getConvertImageCommand(), original, target);
        CommandLine commandLine
                = CommandLine.parse(config.getConvertImageCommand() + " " + original.toString() + " " + target);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        executor.execute(commandLine);
        return Paths.get(target);
    }

}
