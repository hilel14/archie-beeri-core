package org.hilel14.archie.beeri.core.storage;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * Each method takes at least 2 parameters:
 *
 * repository: Repository code. One of: public, private, secret, import, mail.
 *
 * uri: A relative path of a resources inside its repository. Usually a
 * combination of some prefix (import folder name, originals, thumbnails ,text)
 * and a file name.
 *
 * @author hilel14
 */
public interface StorageConnector {

    public void setup(Properties props) throws Exception;

    public List<String> list(String repository, URI target) throws Exception;

    public void upload(Path source, String repository, URI target) throws Exception;

    public Path download(String repository, URI source) throws Exception;

    public void delete(String repository, URI target) throws Exception;

    public void move(String sourceRepository, String targetRepository, URI uri) throws Exception;

    public boolean exist(String repository, URI source);

}
