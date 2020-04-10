package org.hilel14.archie.beeri.core.jobs.tasks;

import java.io.IOException;
import org.hilel14.archie.beeri.core.Config;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.hilel14.archie.beeri.core.jobs.model.ImportFileTicket;
import org.hilel14.archie.beeri.core.jobs.tools.OcrTool;
import org.hilel14.archie.beeri.core.jobs.tools.PdfTool;

/**
 *
 * @author hilel14
 */
public class ContentExtractor implements TaskProcessor {

    static final Logger LOGGER = LoggerFactory.getLogger(ContentExtractor.class);
    final Config config;
    PdfTool pdfTool;
    OcrTool ocrTool;

    public ContentExtractor(Config config) throws IOException {
        this.config = config;
        pdfTool = new PdfTool(config);
        ocrTool = new OcrTool(config);
    }

    @Override
    public void proccess(ImportFileTicket ticket) throws Exception {
        switch (ticket.getImportFolderForm().getTextAction()) {
            case "recognize":
                ocrTool.recognizeTextWithOcr(ticket);
                break;
            case "extract":
                pdfTool.extractTextFromPdf(ticket);
                break;
            default:
                LOGGER.debug("Skipping text action for file {}", ticket.getFileName());
        }
    }

}
