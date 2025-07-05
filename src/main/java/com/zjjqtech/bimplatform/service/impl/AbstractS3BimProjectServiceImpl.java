package com.zjjqtech.bimplatform.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjjqtech.bimplatform.infrastructure.exception.BizException;
import com.zjjqtech.bimplatform.model.BimModel;
import com.zjjqtech.bimplatform.model.BimProject;
import com.zjjqtech.bimplatform.model.FileUploadArgs;
import com.zjjqtech.bimplatform.repository.BimProjectRepository;
import com.zjjqtech.bimplatform.repository.TagRepository;
import com.zjjqtech.bimplatform.repository.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class AbstractS3BimProjectServiceImpl extends AbstractBimProjectServiceImpl {

    protected abstract void deleteModelFiles(String prefix);

    protected abstract FileUploadArgs buildModelFileUploadArgs(String type, String bimProjectId, String modelName, String fileName, String contentType);

    protected abstract String generateCover(String path, String type, String id, String name, InputStream inputStream, long size, String contentType);

    @Value("${s3.expired-seconds}")
    protected long expiredSeconds;
    @Value("${s3.endpoint}")
    protected String endpoint;
    @Value("${s3.external-endpoint}")
    protected String externalEndpoint;
    @Value("${s3.accessKeyId}")
    protected String accessKeyId;
    @Value("${s3.accessKeySecret}")
    protected String accessKeySecret;
    @Value("${s3.bucketName}")
    protected String bucketName;
    @Autowired
    protected ObjectMapper objectMapper;

    public AbstractS3BimProjectServiceImpl(BimProjectRepository bimProjectRepository, TagRepository tagRepository, UserRepository userRepository) {
        super(bimProjectRepository, tagRepository, userRepository);
    }

    @Override
    public FileUploadArgs getModelFileUploadArgs(String path, String type, String bimProjectId, String modelName, String fileName, String contentType) {
        if (StringUtils.isEmpty(contentType)) {
            contentType = "application/octet-stream";
        }
        Optional<BimProject> bimProjectHolder = bimProjectRepository.findById(bimProjectId);
        if (bimProjectHolder.isPresent()) {
            BimProject bimProject = bimProjectHolder.get();
            List<BimModel> models = bimProject.getModels();
            Optional<BimModel> modelHolder = models.stream().filter(m -> modelName.equals(m.getName())).findFirst();
            FileUploadArgs fileUploadArgs = buildModelFileUploadArgs(type, bimProjectId, modelName, fileName, contentType);
            if (fileName.lastIndexOf(type) > -1) {
                if (modelHolder.isPresent()) {
                    modelHolder.get().setMainPath(generateMainPath(path, type, bimProjectId, modelName, fileName));
                } else {
                    BimModel model = new BimModel();
                    model.setType(type);
                    model.setName(modelName);
                    model.setPrefix(generatePrefix(type, bimProjectId, modelName));
                    model.setMainPath(generateMainPath(path, type, bimProjectId, modelName, fileName));
                    models.add(model);
                }
                bimProjectRepository.save(bimProject);
            }
            return fileUploadArgs;
        } else {
            throw new BizException("validate.error.bim-project.non-existed");
        }
    }


    @Override
    public void setMainModel(String type, String bimProjectId, String modelName) {
        Optional<BimProject> bimProjectHolder = bimProjectRepository.findById(bimProjectId);
        if (bimProjectHolder.isPresent()) {
            BimProject bimProject = bimProjectHolder.get();
            List<BimModel> models = bimProject.getModels();
            OptionalInt indexHolder = IntStream.range(0, models.size()).filter(i -> modelName.equals(models.get(i).getName())).findFirst();
            if (indexHolder.isPresent()) {
                Collections.swap(models, 0, indexHolder.getAsInt());
                bimProjectRepository.save(bimProject);
            } else {
                throw new BizException("validate.error.bim-model.non-existed");
            }
        } else {
            throw new BizException("validate.error.bim-project.non-existed");
        }
    }

    @Override
    public void renameModel(String type, String bimProjectId, String oldName, String newName) {
        Optional<BimProject> bimProjectHolder = bimProjectRepository.findById(bimProjectId);
        if (newName.equals(oldName)) {
            return;
        }
        if (bimProjectHolder.isPresent()) {
            BimProject bimProject = bimProjectHolder.get();
            List<BimModel> models = bimProject.getModels();
            Optional<BimModel> modelHolder = models.stream().filter(m -> oldName.equals(m.getName())).findFirst();
            if (modelHolder.isPresent()) {
                if (models.stream().map(BimModel::getName).noneMatch(newName::equals)) {
                    modelHolder.get().setName(newName);
                    bimProjectRepository.save(bimProject);
                } else {
                    throw new BizException("validate.error.bim-model.existed");
                }
            } else {
                throw new BizException("validate.error.bim-model.non-existed");
            }
        } else {
            throw new BizException("validate.error.bim-project.non-existed");
        }
    }

    @Override
    public void deleteModel(String type, String bimProjectId, String modelName) {
        Optional<BimProject> bimProjectHolder = bimProjectRepository.findById(bimProjectId);
        if (bimProjectHolder.isPresent()) {
            BimProject bimProject = bimProjectHolder.get();
            List<BimModel> models = bimProject.getModels();
            Optional<BimModel> modelHolder = models.stream().filter(m -> modelName.equals(m.getName())).findFirst();
            if (modelHolder.isPresent()) {
                deleteModelFiles(modelHolder.get().getPrefix());
                models.removeIf(m -> modelName.equals(m.getName()));
                bimProjectRepository.save(bimProject);
            } else {
                deleteModelFiles(generatePrefix(type, bimProjectId, modelName));
            }
        } else {
            throw new BizException("validate.error.bim-project.non-existed");
        }
    }


    @SneakyThrows
    @Override
    public void setCover(String path, String type, String id, String name, InputStream inputStream, long size, String contentType) {
        Optional<BimProject> bimProjectHolder = bimProjectRepository.findById(id);
        if (bimProjectHolder.isPresent()) {
            BimProject bimProject = bimProjectHolder.get();
            String coverUrl = generateCover(path, type, id, name, inputStream, size, contentType);
            JsonNode jsonNode = bimProject.getExt();
            if (null == jsonNode) {
                jsonNode = objectMapper.valueToTree(new HashMap<>());
            }
            Map<String, Object> map = objectMapper.treeToValue(jsonNode, Map.class);
            map.put("coverUrl", coverUrl);
            bimProject.setExt(objectMapper.valueToTree(map));
            bimProjectRepository.save(bimProject);
        } else {
            throw new BizException("validate.error.bim-project.non-existed");
        }
    }

    protected String generatePrefix(String type, String bimProjectId, String modelName) {
        return Stream.of(type, bimProjectId, modelName).filter(Objects::nonNull).collect(Collectors.joining("/"));
    }

    protected String generateObjectName(String type, String bimProjectId, String modelName, String mainFile) {
        return String.join("/", generatePrefix(type, bimProjectId, modelName), mainFile);
    }

    protected String generateMainPath(String path, String type, String bimProjectId, String modelName, String mainFile) {
        return String.join("/", path, generateObjectName(type, bimProjectId, modelName, mainFile));
    }


}
