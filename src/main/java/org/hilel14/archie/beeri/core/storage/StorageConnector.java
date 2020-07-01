package org.hilel14.archie.beeri.core.storage;

import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * Each method takes at least 2 parameters:
 *
 * repository: Repository code. One of: public, private, secret, import, mail.
 *
 * container: Folder or container name (originals, thumbnails ,text or the name
 * of a specific import folder).
 *
 *
 * @author hilel14
 */
public interface StorageConnector {

    public void setup(Properties props) throws Exception;

    public List<String> listFolders(String repository, String container) throws Exception;
    
    public List<String> listFiles(String repository, String container) throws Exception;

    public void upload(Path source, String repository, String container) throws Exception;

    public Path download(String repository, String container, String file) throws Exception;

    public void delete(String repository, String container, String file) throws Exception;

    public void move(String sourceRepository, String targetRepository, String container, String file) throws Exception;

    public boolean exist(String repository, String container, String file);

}
