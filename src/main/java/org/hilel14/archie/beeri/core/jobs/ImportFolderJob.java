package org.hilel14.archie.beeri.core.jobs;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.tasks.ContentExtractor;
import org.hilel14.archie.beeri.core.jobs.tasks.DigestCalculator;
import org.hilel14.archie.beeri.core.jobs.tasks.DocumentCreator;
import org.hilel14.archie.beeri.core.jobs.tasks.DuplicateFinder;
import org.hilel14.archie.beeri.core.jobs.tasks.FileInstaller;
import org.hilel14.archie.beeri.core.jobs.tasks.FileValidator;
import org.hilel14.archie.beeri.core.jobs.model.ImportFileTicket;
import org.hilel14.archie.beeri.core.jobs.model.ImportFolderForm;
import org.hilel14.archie.beeri.core.jobs.tasks.TaskProcessor;
import org.hilel14.archie.beeri.core.jobs.tools.DatabaseTool;
import org.hilel14.archie.beeri.core.storage.StorageConnector;

/**
 *
 * @author hilel14
 */
public class ImportFolderJob {

    static final Logger LOGGER = LoggerFactory.getLogger(ImportFolderJob.class);
    Config config;
    DatabaseTool databaseTool;
    List<TaskProcessor> processors = new ArrayList<>();

    public static void main(String[] args) {
        Path in = Paths.get(args[0]);
        try {
            Config config = new Config();
            String jobSpec = new String(Files.readAllBytes(in));
            ImportFolderForm form = ImportFolderForm.unmarshal(jobSpec);
            ImportFolderJob job = new ImportFolderJob(config);
            job.run(form);
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }

    public ImportFolderJob(Config config) throws IOException {
        this.config = config;
        databaseTool = new DatabaseTool(config);
        initProcessors();
    }

    private void initProcessors() throws IOException {
        processors.add(new FileValidator(config));
        processors.add(new DigestCalculator(config));
        processors.add(new DuplicateFinder(config));
        processors.add(new ContentExtractor(config));
        processors.add(new DocumentCreator(config));
        processors.add(new FileInstaller(config));
    }

    public void run(ImportFolderForm form) throws Exception {
        LOGGER.info("Importing folder {}", form.getFolderName());
        String folder = URLEncoder.encode(form.getFolderName(), StandardCharsets.UTF_8.toString());
        URI uri = URI.create(folder);
        List<String> items = config.getStorageConnector().list("import", uri);
        LOGGER.debug("Folder {} contains {} items, textAction = {}, addFileNamesTo = {}",
                form.getFolderName(), items.size(), form.getTextAction(), form.getAddFileNamesTo());
        databaseTool.createImportFolderRecord(form, items.size());
        StorageConnector connector = config.getStorageConnector();
        for (String item : items) {
            // prepare
            ImportFileTicket ticket = new ImportFileTicket(item, form);
            databaseTool.createImportFileRecord(ticket);
            // download            
            String file = URLEncoder.encode(ticket.getFileName(), StandardCharsets.UTF_8.toString());
            URI source = URI.create(folder).resolve(file);
            Path path = connector.download("import", source);
            // import
            importFile(ticket, path);
            databaseTool.updateImportFileRecord(ticket);
        }
        LOGGER.info("Import job completed successfully for folder {}", form.getFolderName());
    }

    public void importFile(ImportFileTicket ticket, Path path) throws Exception {
        for (TaskProcessor processor : processors) {
            if (ticket.getImportStatusCode() == ImportFileTicket.IMPORT_IN_PROGRESS) {
                processor.proccess(ticket, path);
            }
        }
        ticket.finalizeStatus();
    }

}
