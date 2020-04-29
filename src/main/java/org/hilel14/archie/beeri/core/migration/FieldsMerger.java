package org.hilel14.archie.beeri.core.migration;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Merge fields values from 2 files or more, into one output file, ready for
 * UpdateDocuments job.
 *
 * @author hilel14
 */
public class FieldsMerger {

    static final Logger LOGGER = LoggerFactory.getLogger(FieldsMerger.class);

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Mandatory arguments: input-folder, target-field");
            System.out.println("");
            System.exit(1);
        }
        Path inFolder = Paths.get(args[0]);
        String targeField = args[1];
        try {
            mergeFields(inFolder, targeField);
        } catch (IOException ex) {
            LOGGER.error("Error while merging files", ex);
        }
    }

    static void mergeFields(Path inFolder, String targeField) throws IOException {
        LOGGER.info("Merging csv files from {}", inFolder);
        Map<String, String> map = new HashMap<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inFolder, "*.csv")) {
            for (Path path : stream) {
                LOGGER.info("Processing file {}", path.getFileName());
                try (FileReader reader = new FileReader(path.toFile(), Charset.forName("utf-8"))) {
                    Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
                    for (CSVRecord record : records) {
                        String id = record.get(0);
                        String val = map.containsKey(id)
                                ? map.get(id) + "," + record.get(1)
                                : record.get(1);
                        map.put(id, val);
                    }
                }
            }
            Path targetPath = inFolder.resolve(targeField);
            saveResults(map, targetPath, targeField);
            LOGGER.info("Merged file with {} records saved as {}", map.size(), targetPath);
        }
    }

    static void saveResults(Map<String, String> map, Path targetPath, String targeField) throws IOException {
        try (FileWriter out = new FileWriter(targetPath.toFile(), Charset.forName("utf-8"))) {
            CSVPrinter printer = CSVFormat.DEFAULT.withHeader("id", targeField).print(out);
            for (String key : map.keySet()) {
                printer.print(key);
                printer.print(map.get(key));
                printer.println();
            }
        }

    }

}
