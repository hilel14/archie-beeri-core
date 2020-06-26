package org.hilel14.archie.beeri.core.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.jobs.tasks.TaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hilel14.archie.beeri.core.jobs.tasks.ContentExtractor;
import org.hilel14.archie.beeri.core.jobs.tasks.DigestCalculator;
import org.hilel14.archie.beeri.core.jobs.tasks.DocumentCreator;
import org.hilel14.archie.beeri.core.jobs.tasks.DuplicateFinder;
import org.hilel14.archie.beeri.core.jobs.tasks.FileInstaller;
import org.hilel14.archie.beeri.core.jobs.tasks.FileValidator;
import org.hilel14.archie.beeri.core.jobs.model.ImportFileTicket;
import org.hilel14.archie.beeri.core.jobs.model.ImportFolderForm;

/**
 *
 * @author hilel14
 */
public class ImportFileJob {

    static final Logger LOGGER = LoggerFactory.getLogger(ImportFileJob.class);
    final Config config;
    final List<TaskProcessor> processors = new ArrayList<>();

    public ImportFileJob(Config config) throws IOException {
        this.config = config;
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

    public void importFile(ImportFolderForm form, String fileName) throws Exception {
        ImportFileTicket ticket = new ImportFileTicket(fileName, form);
        
        /*
        for (TaskProcessor processor : processors) {
            if (ticket.getImportStatusCode() == ImportFileTicket.IMPORT_IN_PROGRESS) {
                processor.proccess(ticket);
            }
        }
        ticket.finalizeStatus();
         */
    }
}
