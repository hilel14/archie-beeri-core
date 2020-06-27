package org.hilel14.archie.beeri.core.storage;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class SimpleStorageConnector implements StorageConnector {

    static final Logger LOGGER = LoggerFactory.getLogger(SimpleStorageConnector.class);
    private final Map<String, Path> repositories = new HashMap<>();
    private Path workFolder;

    @Override
    public void setup(Properties p) {
        repositories.put("public", Paths.get(p.getProperty("public.assets")));
        repositories.put("private", Paths.get(p.getProperty("private.assets")));
        repositories.put("secret", Paths.get(p.getProperty("secret.assets")));
        repositories.put("import", Paths.get(p.getProperty("import.folder")));
        repositories.put("mail", Paths.get(p.getProperty("mail.folder")));
        workFolder = Paths.get(p.getProperty("work.folder"));
    }

    @Override
    public List<String> list(String repository, String container) throws IOException {
        List<String> items = new ArrayList<>();
        Path dir = repositories.get(repository).resolve(container);
        DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
        for (Path path : stream) {
            items.add(path.getFileName().toString());
        }
        return items;
    }

    @Override
    public void upload(Path source, String repository, String container) throws IOException {
        Files.copy(source, repositories.get(repository).resolve(container).resolve(source.getFileName()));
    }

    @Override
    public Path download(String repository, String container, String file) throws IOException {
        return repositories.get(repository).resolve(container).resolve(file);
    }

    @Override
    public void delete(String repository, String container, String file) throws IOException {
        Files.deleteIfExists(repositories.get(repository).resolve(container).resolve(file));
    }

    @Override
    public void move(String sourceRepository, String targetRepository, String container, String file)
            throws IOException {
        Path source = repositories.get(sourceRepository).resolve(container).resolve(file);
        Path target = repositories.get(targetRepository).resolve(container).resolve(file);
        Files.move(source, target);
    }

    @Override
    public boolean exist(String repository, String container, String file) {
        Path path = repositories.get(repository).resolve(container).resolve(file);
        return Files.exists(path);
    }

    /**
     * @return the workFolder
     */
    public Path getWorkFolder() {
        return workFolder;
    }

}
