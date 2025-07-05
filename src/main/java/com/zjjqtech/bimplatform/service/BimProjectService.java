package com.zjjqtech.bimplatform.service;

import com.zjjqtech.bimplatform.model.BimProject;
import com.zjjqtech.bimplatform.model.BimProjectAbbr;
import com.zjjqtech.bimplatform.model.FileUploadArgs;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.List;

/**
 * @author zao
 * @date 2020/09/21
 */
@Validated
public interface BimProjectService {
    /**
     * find
     *
     * @param name name
     * @return bim
     */
    BimProject find(String name);

    /**
     * save
     *
     * @param bimProject bim
     * @return bim
     */
    BimProject save(@Valid BimProject bimProject);

    /**
     * delete
     *
     * @param id id
     */
    void delete(String id);

    /**
     * find
     *
     * @param nameLike nameLike
     * @param tags     tags
     * @param pageable pageable
     * @return page
     */
    Page<BimProjectAbbr> find(String nameLike, List<String> tags, Pageable pageable);

    /**
     * find
     *
     * @param userId   userId
     * @param nameLike nameLike
     * @param tags     tags
     * @param pageable pageable
     * @return page
     */
    Page<BimProjectAbbr> find(String userId, String nameLike, List<String> tags, Pageable pageable);


    /**
     * setMainModel
     *
     * @param type         type
     * @param bimProjectId bimProjectId
     * @param modelName    modelName
     */
    void setMainModel(String type, String bimProjectId, String modelName);


    /**
     * renameModel
     *
     * @param type         type
     * @param bimProjectId bimProjectId
     * @param oldName      oldName
     * @param newName      newName
     */
    void renameModel(String type, String bimProjectId, String oldName, String newName);


    /**
     * deleteModel
     *
     * @param type         type
     * @param bimProjectId bimProjectId
     * @param modelName    modelName
     */
    void deleteModel(String type, String bimProjectId, String modelName);

    /**
     * getModelFileUploadUrl
     *
     * @param path        path
     * @param type        type
     * @param id          id
     * @param modelName   modelName
     * @param fileName    fileName
     * @param contentType contentType
     * @return fileUploadUrl
     */
    FileUploadArgs getModelFileUploadArgs(String path, String type, String id, String modelName, String fileName, String contentType);

    /**
     * checkIsOwnerOfBimProject
     *
     * @param bimProjectId bimProjectId
     * @return is or not
     */
    boolean checkIsOwnerOfBimProject(String bimProjectId);

    /**
     * canAccess
     *
     * @param bimProjectId bimProjectId
     * @return canAccess
     */
    boolean canAccess(String bimProjectId);

    /**
     * canAccess
     *
     * @param bimProjectId bimProjectId
     * @param shareToken   shareToken
     * @return canAccess
     */
    boolean canAccess(String bimProjectId, String shareToken);

    /**
     * generateShareToken
     *
     * @param bimProjectId bimProjectId
     * @return shareToken
     */
    String generateShareToken(String bimProjectId);

    /**
     * getFileResource
     *
     * @param type         type
     * @param bimProjectId bimProjectId
     * @param modelName    modelName
     * @param mainFile     mainFile
     * @return inputStream
     */
    ResponseEntity<InputStreamResource> getFileResource(String type, String bimProjectId, String modelName, String mainFile);

    /**
     * setCover
     *
     * @param path        path
     * @param type        type
     * @param id          id
     * @param name        name
     * @param inputStream inputStream
     * @param size        size
     * @param contentType contentType
     */
    void setCover(String path, String type, String id, String name, InputStream inputStream, long size, String contentType);
}
