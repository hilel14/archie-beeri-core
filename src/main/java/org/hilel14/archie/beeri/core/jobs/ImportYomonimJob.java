package org.hilel14.archie.beeri.core.jobs;

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.model.ImportFolderForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class ImportYomonimJob {

    static final Logger LOGGER = LoggerFactory.getLogger(ImportYomonimJob.class);
    final Config config;
    final Pattern ISSUE_NUMBER_PATTERN = Pattern.compile("\\d{5}.*");
    final Pattern REGION_TEXT_DATE_PATTERN = Pattern.compile("\\d{1,2}" + "\\." + "\\d{1,2}" + "\\." + "\\d{2,4}");
    final Pattern FILE_NAME_DATE_PATTERN = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");
    final DateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    final DateFormat ISO8601_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    Path inboxFolder;
    Path attachmentsFolder;
    Path doneFolder;
    Path importFolder;
    PDFTextStripperByArea stripperByArea;
    ImportFileJob importFileJob;

    public static void main(String[] args) {
        try {
            Config config = new Config();
            ImportYomonimJob job = new ImportYomonimJob(config);
            job.run();
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }

    public ImportYomonimJob(Config config) throws IOException {
        this.config = config;
        inboxFolder = config.getMailFolder().resolve("inbox");
        attachmentsFolder = config.getMailFolder().resolve("attachments");
        doneFolder = config.getMailFolder().resolve("done");
        importFolder = config.getImportFolder().resolve("yomonim");
        Files.createDirectories(importFolder);
        stripperByArea = new PDFTextStripperByArea();
        stripperByArea.addRegion("topRightCorner", new Rectangle(451, 20, 100, 100));
        stripperByArea.setSortByPosition(true);
        importFileJob = new ImportFileJob(config);
    }

    public void run() throws Exception {
        LOGGER.info("{} files found in inbox", inboxFolder.toFile().list().length);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inboxFolder)) {
            for (Path path : stream) {
                extractAttachments(path);
                Path done = doneFolder.resolve(path.getFileName());
                Files.move(path, done);
            }
            LOGGER.info("The operation completed successfully");
        }
    }

    private void extractAttachments(Path inFile) throws Exception {
        LOGGER.debug("Processing mail file {}", inFile.getFileName());
        MimeMessage message = new MimeMessage(null, new FileInputStream(inFile.toFile()));
        final MimeMessageParser mimeMessageParser = new MimeMessageParser((MimeMessage) message);
        mimeMessageParser.parse();
        if (mimeMessageParser.hasAttachments()) {
            List<DataSource> attachmentList = mimeMessageParser.getAttachmentList();
            LOGGER.info("{} attachments found in file {}", attachmentList.size(), inFile.getFileName());
            for (DataSource attachment : attachmentList) {
                LOGGER.debug("Name: {}  Content Type: {}",
                        attachment.getName(), attachment.getContentType());
                if (attachment.getContentType().equals("application/pdf")) {
                    Path outFile = attachmentsFolder.resolve(attachment.getName());
                    FileUtils.copyInputStreamToFile(attachment.getInputStream(), outFile.toFile());
                    processAttachment(outFile);
                } else {
                    LOGGER.warn("Mail file {} "
                            + "contains attachment {} "
                            + "with content-type {}",
                            inFile.getFileName(),
                            attachment.getName(),
                            attachment.getContentType()
                    );
                }
            }
        } else {
            LOGGER.warn(
                    "Mail file {} "
                    + "with subject {} "
                    + "has not attachments",
                    inFile.getFileName(),
                    message.getSubject()
            );
        }
    }

    private void processAttachment(Path inFile) throws Exception {
        String fileName = inFile.getFileName().toString().trim();
        Path outFile = importFolder.resolve(inFile.getFileName());
        LOGGER.debug("Moving attachment {} to import folder", inFile);
        Files.move(inFile, outFile);
        LOGGER.debug("Analyzing file name {}", fileName);
        String[] parts = fileName.split("\\.");
        if (parts.length == 4) {
            if (parts[0].toLowerCase().contains("yomonbeeri")) {
                if (FILE_NAME_DATE_PATTERN.matcher(parts[1]).matches()) {
                    String date = ISO8601_TIME_FORMAT.format(FILE_NAME_DATE_FORMAT.parse(parts[1]));
                    if (ISSUE_NUMBER_PATTERN.matcher(parts[2]).matches()) {
                        if (parts[3].equalsIgnoreCase("pdf")) {
                            importFile(outFile, date, parts[2]);
                            return;
                        }
                    }
                }
            }
        }
        LOGGER.warn("Invalid file name {}", fileName);
    }

    private void importFile(Path path, String date, String issue) throws Exception {
        Path p = path.getParent().resolve(issue + ".pdf");
        Files.move(path, p);
        ImportFolderForm form = new ImportFolderForm();
        form.setAddFileNamesTo("dcTitle");
        form.setDcAccessRights("private");
        form.setDcDate(date);
        form.setDcIsPartOf("עיתונות מקומית >> יומון בארי");
        form.setDcTitle("יומון ");
        form.setDcType("text");
        form.setFolderName("yomonim");
        form.setTextAction("extract");
        importFileJob.importFile(form, p.getFileName().toString());
    }

    private void extractRegion(Path inFile)
            throws IOException, ParserConfigurationException, TransformerException, ParseException {
        LOGGER.debug("Extracting text from file {}", inFile.getFileName());
        try (PDDocument doc = PDDocument.load(inFile.toFile())) {
            // extract text area for later use
            stripperByArea.extractRegions(doc.getPage(0));
            String data = stripperByArea.getTextForRegion("topRightCorner");
            data = data.replace("\n", " ").trim();
            LOGGER.debug("data {}", data);
            // find issue number
            Matcher matcher = ISSUE_NUMBER_PATTERN.matcher(data);
            if (matcher.find()) {
                LOGGER.debug("issue number {}", matcher.group(0));
            }
            // find date
            matcher = REGION_TEXT_DATE_PATTERN.matcher(data);
            if (matcher.find()) {
                LOGGER.debug("date {}", matcher.group(0));
            }
        }
    }

}
