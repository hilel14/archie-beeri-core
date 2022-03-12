package org.hilel14.archie.beeri.core.storage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 *
 * @author hilel14
 */
public class AwsBucketConnector implements StorageConnector {

    static final Logger LOGGER = LoggerFactory.getLogger(AwsBucketConnector.class);

    private S3Client s3;
    private final Map<String, String> repositories = new HashMap<>();
    private Path workFolder;

    @Override
    public void setup(Properties p) throws Exception {
        s3 = S3Client.create();
        repositories.put("public", p.getProperty("public.assets"));
        repositories.put("private", p.getProperty("private.assets"));
        repositories.put("secret", p.getProperty("secret.assets"));
        repositories.put("import", p.getProperty("import.folder"));
        repositories.put("mail", p.getProperty("mail.folder"));
        workFolder = Paths.get(p.getProperty("work.folder"));
    }

    @Override
    public List<String> listFolders(String repository, String container) throws Exception {
        LOGGER.debug("listing folders in repository {} container {}", repositories.get(repository), container);
        List<String> items = new ArrayList<>();
        ListObjectsRequest.Builder builder = ListObjectsRequest.builder();
        builder = builder.bucket(repositories.get(repository))
                .delimiter("/");
        if (!container.isEmpty()) {
            builder = builder.prefix(container);
        }
        ListObjectsRequest request = builder.build();
        ListObjectsResponse response = s3.listObjects(request);
        List<CommonPrefix> folders = response.commonPrefixes();
        for (CommonPrefix folder : folders) {
            items.add(folder.prefix().substring(0, folder.prefix().length() - 1));
        }
        LOGGER.debug("items count {}", items.size());
        return items;
    }

    @Override
    public List<String> listFiles(String repository, String container) throws Exception {
        LOGGER.debug("listing files in repository {} container {}", repositories.get(repository), container);
        List<String> items = new ArrayList<>();
        ListObjectsRequest request = ListObjectsRequest
                .builder()
                .bucket(repositories.get(repository))
                .prefix(container)
                .build();
        ListObjectsResponse response = s3.listObjects(request);
        List<S3Object> objects = response.contents();
        for (ListIterator iterVals = objects.listIterator(); iterVals.hasNext();) {
            S3Object object = (S3Object) iterVals.next();
            if (!object.key().endsWith("/")) {
                String prefix = container.concat("/");
                items.add(object.key().replace(prefix, ""));
            }
        }
        return items;
    }

    @Override
    public void upload(Path source, String repository,
            String container) throws Exception {
        String file = source.getFileName().toString();
        String key = container.isEmpty() ? file : container.concat("/").concat(file);
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(repositories.get(repository))
                .key(key)
                .contentDisposition("attachment")
                .build();
        PutObjectResponse response = s3.putObject(
                request,
                RequestBody.fromFile(source)
        );
    }

    @Override
    public Path download(String repository, String container,
            String file) throws Exception {
        String key = container.isEmpty() ? file : container.concat("/").concat(file);
        Path target = workFolder.resolve(repository).resolve(file);
        //Files.createDirectories(target.getParent());
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(repositories.get(repository))
                .key(key)
                .build();
        GetObjectResponse response = s3.getObject(request, target);
        return target;
    }

    @Override
    public void delete(String repository, String container,
            String file) throws Exception {
        String key = container.isEmpty() ? file : container.concat("/").concat(file);
        ArrayList<ObjectIdentifier> toDelete = new ArrayList<>();
        toDelete.add(ObjectIdentifier.builder().key(key).build());
        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(repositories.get(repository))
                .delete(Delete.builder().objects(toDelete).build())
                .build();
        s3.deleteObjects(request);
    }

    @Override
    public void move(String sourceRepository, String targetRepository,
            String container, String file) throws Exception {
        String key = container.isEmpty() ? file : container.concat("/").concat(file);
        String source = repositories.get(sourceRepository).concat("/").concat(key);
        LOGGER.debug("moving {} to bucket {} key {}",
                source,
                repositories.get(targetRepository),
                key
        );
        CopyObjectRequest request = CopyObjectRequest.builder()
                .copySource(source)
                .destinationBucket(repositories.get(targetRepository))
                .destinationKey(key)
                .build();
        CopyObjectResponse response = s3.copyObject(request);
        delete(sourceRepository, container, file);
    }

    @Override
    public boolean exist(String repository, String container, String file) {
        String key = container.isEmpty() ? file : container.concat("/").concat(file);
        HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(repositories.get(repository))
                .key(key)
                .build();
        try {
            HeadObjectResponse response = s3.headObject(request);
            LOGGER.debug("Object {} size {}", key, response.contentLength());
            return true;
        } catch (NoSuchKeyException e) {
            LOGGER.debug("NoSuchKey {}", key);
        }
        return false;
    }

}
