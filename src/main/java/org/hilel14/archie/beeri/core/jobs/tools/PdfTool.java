package org.hilel14.archie.beeri.core.jobs.tools;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.hilel14.archie.beeri.core.Config;

import org.hilel14.archie.beeri.core.jobs.model.ImportFileTicket;

/**
 *
 * @author hilel14
 */
public class PdfTool {

    static final Logger LOGGER = LoggerFactory.getLogger(PdfTool.class);
    final Config config;

    public PdfTool(Config config) {
        this.config = config;
    }

    public void extractTextFromPdf(ImportFileTicket ticket, Path path) throws IOException {
        if (ticket.getFormat().equalsIgnoreCase("pdf")) {
            LOGGER.debug("Extracting text from PDF file {}", ticket.getFileName());
            try (PDDocument doc = PDDocument.load(path.toFile())) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                Writer writer = new StringWriter();
                stripper.writeText(doc, writer);
                String result = writer.toString();
                if (result != null) {
                    ticket.setContent(result.trim());
                    Path target = config.getWorkFolder().resolve("import").resolve(ticket.getUuid() + ".txt");
                    Files.writeString(target, result, Charset.forName("utf-8"), StandardOpenOption.CREATE_NEW);
                }
            }
        } else {
            LOGGER.warn("{} is not a PDF file", ticket.getFileName());
        }
    }
}
