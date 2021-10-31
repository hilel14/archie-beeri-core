package org.hilel14.archie.beeri.core.cli;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrepareUpdate {
    static final Logger LOGGER = LoggerFactory.getLogger(PrepareUpdate.class);

    public static void main(String[] args) {
        Path inPath = Paths.get(args[0]);
        try {
            csvToJason(inPath);
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }

    private static void csvToJason(Path inPath) throws Exception {
        LOGGER.info("Converting CSV file {} to Json ", inPath);
        int count = 0;
        try (Reader in = new FileReader(inPath.toFile());
                FileOutputStream out = new FileOutputStream(inPath.toString().concat(".json"));) {
            JsonFactory factory = new JsonFactory();
            JsonGenerator generator = factory.createGenerator(out, JsonEncoding.UTF8);
            generator.useDefaultPrettyPrinter();
            generator.writeStartArray();
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                count++;
                convertDoc(record.toMap(), generator);
            }
            generator.writeEndArray();
            generator.close();
            LOGGER.info("The operation completed successfully. {} documents converted.", count);
        }
    }

    private static void convertDoc(Map<String, String> map, JsonGenerator generator) throws Exception {
        LOGGER.info("Processing record {}", map.get("id"));
        generator.writeStartObject();
        generator.writeStringField("id", map.get("id")); // TODO: validate id (not empty, regex?)
        setString(generator, "dcTitle", map);
        setString(generator, "dcDate", map);
        setString(generator, "dcType", map);
        setString(generator, "dcFormat", map);
        setString(generator, "storageLocation", map);
        setString(generator, "dcIsPartOf", map);
        setString(generator, "dcAccessRights", map);
        setArray(generator, "dcCreator", map);
        setArray(generator, "dcSubject", map);
        generator.writeEndObject();
    }

    private static void setString(JsonGenerator generator, String key, Map<String, String> record) throws IOException {
        if (record.containsKey(key)) {
            String text = record.get(key).trim();
            generator.writeFieldName(key);
            generator.writeStartObject();
            generator.writeStringField("set", text.isEmpty() ? null : text);
            generator.writeEndObject();
        }
    }

    private static void setArray(JsonGenerator generator, String key, Map<String, String> record) throws IOException {
        if (record.containsKey(key)) {
            List<String> list = processArray(record.get(key));
            generator.writeFieldName(key);
            generator.writeStartObject();
            if (list.isEmpty()) {
                generator.writeStringField("set", null);
            } else {
                generator.writeFieldName("set");
                generator.writeStartArray();
                for (String item : list) {
                    generator.writeString(item);
                }
                generator.writeEndArray();
            }
            generator.writeEndObject();
        }
    }

    private static List<String> processArray(String text) {
        List<String> list = new ArrayList<>();
        String[] elements = text.trim().split(",");
        if (elements.length > 0) {
            for (String element : elements) {
                if (!element.trim().isEmpty()) {
                    list.add(element.trim());
                }
            }
        }
        return list;
    }
}
