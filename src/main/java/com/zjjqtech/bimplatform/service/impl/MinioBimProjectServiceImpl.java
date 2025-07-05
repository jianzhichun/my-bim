package com.zjjqtech.bimplatform.service.impl;

import com.zjjqtech.bimplatform.model.FileUploadArgs;
import com.zjjqtech.bimplatform.repository.BimProjectRepository;
import com.zjjqtech.bimplatform.repository.TagRepository;
import com.zjjqtech.bimplatform.repository.UserRepository;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Profile("minio")
@Service("bimProjectService")
@Transactional(rollbackOn = Exception.class)
public class MinioBimProjectServiceImpl extends AbstractS3BimProjectServiceImpl {

    private MinioClient minio;

    public MinioBimProjectServiceImpl(BimProjectRepository bimProjectRepository, TagRepository tagRepository,
            UserRepository userRepository) {
        super(bimProjectRepository, tagRepository, userRepository);
    }

    @SneakyThrows
    @PostConstruct
    public void postConstruct() {
        log.info("accessKeyId: {}, accessKeySecret: {}", accessKeyId, accessKeySecret);
        this.minio = MinioClient.builder().endpoint(endpoint).credentials(accessKeyId, accessKeySecret).build();
        if (!this.minio.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            this.minio.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    @SneakyThrows
    @Override
    protected FileUploadArgs buildModelFileUploadArgs(String type, String bimProjectId, String modelName,
            String fileName, String contentType) {
        String objectName = generatePrefix(type, bimProjectId, modelName) + "/" + fileName;
        PostPolicy postPolicy = new PostPolicy(bucketName, ZonedDateTime.now().plusSeconds(expiredSeconds));
        postPolicy.addEqualsCondition("key", objectName);
        postPolicy.addEqualsCondition("Content-Type", contentType);
        Map<String, String> map = minio.getPresignedPostFormData(postPolicy);
        map.put("key", objectName);
        map.put("Content-Type", contentType);
        return new FileUploadArgs(externalEndpoint + "/" + bucketName, map);
    }

    @SneakyThrows
    @Override
    protected void deleteModelFiles(String prefix) {
        List<DeleteObject> objects = new ArrayList<>();
        for (Result<Item> result : this.minio
                .listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(true).build())) {
            objects.add(new DeleteObject(result.get().objectName()));
        }
        for (Result<DeleteError> errorResult : this.minio
                .removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build())) {
            DeleteError error = errorResult.get();
            log.error("Error in deleting bucket: {} object: {}, message, {}", error.bucketName(), error.objectName(),
                    error.message());
        }
    }

    @SneakyThrows
    @Override
    public ResponseEntity<InputStreamResource> getFileResource(String type, String bimProjectId, String modelName,
            String mainFile) {
        String objectName = generateObjectName(type, bimProjectId, modelName, mainFile);
        io.minio.StatObjectResponse response = this.minio
                .statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        InputStreamResource inputStreamResource = new InputStreamResource(
                this.minio.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build()));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(response.headers().toMultimap());
        return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.OK);
    }

    @SneakyThrows
    @Override
    protected String generateCover(String path, String type, String id, String name, InputStream inputStream, long size,
            String contentType) {
        String objectName = generatePrefix(type, id, name);
        this.minio.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).contentType(contentType)
                .stream(inputStream, size, -1).build());
        return path + "/" + objectName;
    }

}
