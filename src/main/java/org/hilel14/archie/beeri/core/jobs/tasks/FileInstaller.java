package org.hilel14.archie.beeri.core.jobs.tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.Config.AccessRights;
import org.hilel14.archie.beeri.core.Config.AssetContainer;
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
    public void proccess(ImportFileTicket ticket) throws Exception {
        moveFileToAssetstore(ticket);
        generatePreview(ticket);
    }

    private void moveFileToAssetstore(ImportFileTicket ticket) throws IOException {
        LOGGER.debug("Moving file {} to asset-store", ticket.getFileName());
        Path source
                = config.getImportFolder()
                        .resolve(ticket.getImportFolderForm().getFolderName())
                        .resolve(ticket.getFileName());
        Path target
                = config
                        .getAssetFolder(getAccessRights(ticket), AssetContainer.ASSETS)
                        .resolve(ticket.getAssetName());
        Files.move(source, target);
    }

    private void generatePreview(ImportFileTicket ticket) throws Exception {
        LOGGER.debug("Generating preview for file {}", ticket.getFileName());
        switch (ticket.getFormat()) {
            case "pdf":
                convertPdf(ticket);
                break;
            case "jpg":
            case "jpeg":
            case "gif":
            case "tif":
            case "tiff":
            case "png":
                convertImage(ticket);
                break;
            default:
                LOGGER.debug("Unable to create preview for {} files", ticket.getFormat());
        }
    }

    private AccessRights getAccessRights(ImportFileTicket ticket) {
        return AccessRights.valueOf(ticket.getImportFolderForm().getDcAccessRights().toUpperCase());
    }

    private void convertPdf(ImportFileTicket ticket) throws Exception {
        String source
                = config
                        .getAssetFolder(getAccessRights(ticket), AssetContainer.ASSETS)
                        .resolve(ticket.getAssetName()).toString() + "[0]";
        String target
                = config
                        .getAssetFolder(getAccessRights(ticket), AssetContainer.PREVIEW)
                        .resolve(ticket.getUuid() + ".png").toString();
        LOGGER.debug("Executing command {} {} {}",
                config.getConvertPdfCommand(), source, target);
        CommandLine commandLine
                = CommandLine.parse(config.getConvertPdfCommand() + " " + source + " " + target);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        executor.execute(commandLine);
        Paths.get(target).toFile().setReadable(true, false);
    }

    private void convertImage(ImportFileTicket ticket) throws Exception {
        String source
                = config
                        .getAssetFolder(getAccessRights(ticket), AssetContainer.ASSETS)
                        .resolve(ticket.getAssetName()).toString();
        String target
                = config
                        .getAssetFolder(getAccessRights(ticket), AssetContainer.PREVIEW)
                        .resolve(ticket.getUuid() + ".png").toString();
        LOGGER.debug("Executing command {} {} {}",
                config.getConvertImageCommand(), source, target);
        CommandLine commandLine
                = CommandLine.parse(config.getConvertImageCommand() + " " + source + " " + target);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);
        executor.execute(commandLine);
        Paths.get(target).toFile().setReadable(true, false);
    }

}
