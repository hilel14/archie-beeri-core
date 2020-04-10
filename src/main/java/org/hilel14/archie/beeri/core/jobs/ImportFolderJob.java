package org.hilel14.archie.beeri.core.jobs;

import java.io.IOException;
import java.nio.file.DirectoryStream;
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
            String attributes = new String(Files.readAllBytes(in));
            ImportFolderJob job = new ImportFolderJob(config);
            job.run(attributes);
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

    public void run(String attributes) throws Exception {
        ImportFolderForm form = ImportFolderForm.unmarshal(attributes);
        LOGGER.info("Importing folder {}", form.getFolderName());
        List<String> items = getFolderItems(form.getFolderName());
        LOGGER.debug("Folder {} contains {} items, textAction = {}, addFileNamesTo = {}",
                form.getFolderName(), items.size(), form.getTextAction(), form.getAddFileNamesTo());
        databaseTool.createImportFolderRecord(form, items.size());
        //DcCollectionsTool.autoCreateCollections(form.getDcIsPartOf());
        for (String item : items) {
            ImportFileTicket ticket = new ImportFileTicket(item, form);
            databaseTool.createImportFileRecord(ticket);
            for (TaskProcessor processor : processors) {
                if (ticket.getImportStatusCode() == ImportFileTicket.IMPORT_IN_PROGRESS) {
                    processor.proccess(ticket);
                }
            }
            ticket.finalizeStatus();
            databaseTool.updateImportFileRecord(ticket);
        }
        LOGGER.info("Import job completed successfully for folder {}", form.getFolderName());
    }

    private List<String> getFolderItems(String folderName) throws IOException {
        List<String> items = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files
                .newDirectoryStream(config.getImportFolder().resolve(folderName))) {
            for (Path path : stream) {
                items.add(path.getFileName().toString());
            }
        }
        return items;

    }

}
