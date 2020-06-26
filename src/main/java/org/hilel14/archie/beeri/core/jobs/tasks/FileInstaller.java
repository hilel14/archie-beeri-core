package org.hilel14.archie.beeri.core.jobs.tasks;

import java.net.URI;
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
    public void proccess(ImportFileTicket ticket, Path path) throws Exception {
        upload(ticket, path);
        generatePreview(ticket, path);
        Files.deleteIfExists(path);
    }

    private void upload(ImportFileTicket ticket, Path source) throws Exception {
        LOGGER.debug("Moving file {} to asset-store", ticket.getFileName());
        String repository = ticket.getImportFolderForm().getDcAccessRights();
        URI target = URI.create("originals").resolve(ticket.getAssetName());
        // copy and delete
        config.getStorageConnector().upload(source, repository, target);

    }

    private void generatePreview(ImportFileTicket ticket, Path path) throws Exception {
        LOGGER.debug("Generating preview for file {}", ticket.getFileName());
        switch (ticket.getFormat()) {
            case "pdf":
                convertPdf(ticket, path);
                break;
            case "jpg":
            case "jpeg":
            case "gif":
            case "tif":
            case "tiff":
            case "png":
                convertImage(ticket, path);
                break;
            default:
                LOGGER.debug("Unable to create preview for {} files", ticket.getFormat());
        }
    }

    private void convertPdf(ImportFileTicket ticket, Path path) throws Exception {
        String source = path.toString() + "[0]";
        String target = path.getParent().resolve(ticket.getUuid() + ".png").toString();
        LOGGER.debug("Executing command {} {} {}",
                config.getConvertPdfCommand(), source, target);
        CommandLine commandLine
                = CommandLine.parse(config.getConvertPdfCommand() + " " + source + " " + target);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        executor.execute(commandLine);
        Paths.get(target).toFile().setReadable(true, false);
    }

    private void convertImage(ImportFileTicket ticket, Path path) throws Exception {
        String target = path.getParent().resolve(ticket.getUuid() + ".png").toString();
        LOGGER.debug("Executing command {} {} {}",
                config.getConvertImageCommand(), path, target);
        CommandLine commandLine
                = CommandLine.parse(config.getConvertImageCommand() + " " + path.toString() + " " + target);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        executor.execute(commandLine);
        Paths.get(target).toFile().setReadable(true, false);
    }

}
