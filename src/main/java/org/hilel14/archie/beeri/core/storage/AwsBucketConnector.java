package org.hilel14.archie.beeri.core.storage;

import java.net.URI;
import java.nio.file.Files;
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
    private final Map<String, Path> repositories = new HashMap<>();
    private Path workFolder;

    @Override
    public void setup(Properties p) throws Exception {
        s3 = S3Client.create();
        repositories.put("public", Paths.get(p.getProperty("public.assets")));
        repositories.put("private", Paths.get(p.getProperty("private.assets")));
        repositories.put("secret", Paths.get(p.getProperty("secret.assets")));
        repositories.put("import", Paths.get(p.getProperty("import.folder")));
        repositories.put("mail", Paths.get(p.getProperty("mail.folder")));
        workFolder = Paths.get(p.getProperty("work.folder"));
    }

    @Override
    public List<String> list(String repository, URI target) throws Exception {
        List<String> items = new ArrayList<>();
        ListObjectsRequest request = ListObjectsRequest
                .builder()
                .bucket(repository)
                .prefix(target.toString())
                .build();
        ListObjectsResponse response = s3.listObjects(request);
        List<S3Object> objects = response.contents();
        for (ListIterator iterVals = objects.listIterator(); iterVals.hasNext();) {
            S3Object object = (S3Object) iterVals.next();
            items.add(object.key());
        }
        return items;
    }

    @Override
    public void upload(Path source, String repository, URI target) throws Exception {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(repository)
                .key(target.toString())
                .build();
        PutObjectResponse response = s3.putObject(
                request,
                RequestBody.fromFile(source)
        );
    }

    @Override
    public Path download(String repository, URI source) throws Exception {
        Path target = workFolder.resolve(repository).resolve(source.toString());
        Files.createDirectories(target.getParent());
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(repository)
                .key(target.toString())
                .build();
        GetObjectResponse response = s3.getObject(request, target);
        return target;
    }

    @Override
    public void delete(String repository, URI target) throws Exception {
        ArrayList<ObjectIdentifier> toDelete = new ArrayList<>();
        toDelete.add(ObjectIdentifier.builder().key(target.toString()).build());
        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(repository)
                .delete(Delete.builder().objects(toDelete).build())
                .build();
        s3.deleteObjects(request);
    }

    @Override
    public void move(String sourceRepository, String targetRepository, URI uri) throws Exception {
        URI source = URI.create(sourceRepository).resolve(uri);
        CopyObjectRequest request = CopyObjectRequest.builder()
                .copySource(source.toString())
                .destinationBucket(targetRepository)
                .destinationKey(uri.toString())
                .build();
        CopyObjectResponse response = s3.copyObject(request);
        delete(sourceRepository, uri);
    }

    @Override
    public boolean exist(String repository, URI source) {
        HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(repository)
                .key(source.toString())
                .build();
        try {
            HeadObjectResponse response = s3.headObject(request);
            LOGGER.debug("Object {} size {}", source, response.contentLength());
            return true;
        } catch (NoSuchKeyException e) {
            LOGGER.debug("NoSuchKey {}", source);
        }
        return false;
    }

}
