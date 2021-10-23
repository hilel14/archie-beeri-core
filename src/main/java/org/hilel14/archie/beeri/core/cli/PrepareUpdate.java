package org.hilel14.archie.beeri.core.cli;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        inPath = Paths.get(
                "/home/hilel/Projects/GitHub/archie/beeri/archie-beeri-core/src/test/resources/updating-parts-of-documents/data-1.csv");
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
        generator.writeStringField("id", map.get("id"));
        setString(generator, "dcTitle", map.get("dcTitle"));
        setString(generator, "dcDate", map.get("dcDate"));
        setString(generator, "dcType", map.get("dcType"));
        setString(generator, "dcFormat", map.get("dcFormat"));
        setString(generator, "storageLocation", map.get("storageLocation"));
        setString(generator, "dcIsPartOf", map.get("dcIsPartOf"));
        setString(generator, "dcAccessRights", map.get("dcAccessRights"));
        setArray(generator, "dcCreator", map.get("dcCreator"));
        setArray(generator, "dcSubject", map.get("dcSubject"));
        generator.writeEndObject();
    }

    private static void setString(JsonGenerator generator, String key, String val) throws IOException {
        generator.writeFieldName(key);
        generator.writeStartObject();
        generator.writeStringField("set", val);
        generator.writeEndObject();
    }

    private static void setArray(JsonGenerator generator, String key, String val) throws IOException {
        generator.writeFieldName(key);
        generator.writeStartObject();
        generator.writeFieldName("set");
        generator.writeStartArray();
        for (String element : val.split(","))
            generator.writeString(element);
        generator.writeEndArray();
        generator.writeEndObject();
    }

}
