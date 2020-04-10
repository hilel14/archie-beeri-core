package org.hilel14.archie.beeri.core.jobs.tools;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.Config.AccessRights;
import org.hilel14.archie.beeri.core.jobs.model.ImportFileTicket;

/**
 *
 * @author hilel14
 */
public class OcrTool {

    static final Logger LOGGER = LoggerFactory.getLogger(OcrTool.class);
    final Config config;

    public OcrTool(Config config) {
        this.config = config;
    }

    public void recognizeTextWithOcr(ImportFileTicket ticket) throws IOException {
        LOGGER.debug("Using OCR to recognize text from file {}", ticket.getFileName());
        // setup
        Path source
                = config.getImportFolder()
                        .resolve(ticket.getImportFolderForm().getFolderName())
                        .resolve(ticket.getFileName());
        AccessRights access = Config.AccessRights.valueOf(ticket.getImportFolderForm().getDcAccessRights().toUpperCase());
        String target
                = config
                        .getAssetFolder(access, Config.AssetContainer.PREVIEW)
                        .resolve(ticket.getUuid()).toString();
        // ocr
        if (config.getValidOcrFormats().contains(ticket.getFormat().toLowerCase())) {
            recognizeText(source.toString(), target);
        } else if (ticket.getFormat().equalsIgnoreCase("pdf")) {
            Path temp = Files.createTempFile("archie.ocr.", ".tif");
            convertToTif(source.toString(), temp.toString());
            recognizeText(temp.toString(), target);
            Files.deleteIfExists(temp);
        } else {
            LOGGER.warn("{} is not a valid OCR file", ticket.getFileName());
            return;
        }
        // process results
        Path text = Paths.get(target + ".txt");
        String content = new String(Files.readAllBytes(text), Charset.forName("utf-8"));
        if (!content.isEmpty()) {
            ticket.setContent(content);
        }
    }

    private void convertToTif(String source, String target) throws IOException {
        CommandLine cmdLine = new CommandLine(config.getConvertCommand());
        cmdLine.addArgument(source);
        cmdLine.addArgument(target);
        DefaultExecutor executor = new DefaultExecutor();
        int exitValue = executor.execute(cmdLine);
        LOGGER.debug("Command {} executed and return {}", cmdLine, exitValue);
    }

    private void recognizeText(String source, String target) throws IOException {
        CommandLine cmdLine = new CommandLine(config.getTesseractCommand());
        cmdLine.addArgument(source);
        cmdLine.addArgument(target);
        cmdLine.addArgument("-l");
        cmdLine.addArgument("heb");
        LOGGER.debug("Executing command {}", cmdLine.toString());
        DefaultExecutor executor = new DefaultExecutor();
        int exitValue = executor.execute(cmdLine);
        LOGGER.debug("Command {} executed and return {}", cmdLine, exitValue);
    }

}
