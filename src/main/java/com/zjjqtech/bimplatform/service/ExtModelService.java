package com.zjjqtech.bimplatform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zjjqtech.bimplatform.model.ExtModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author zao
 * @date 2020/09/24
 */
public interface ExtModelService {

    /**
     * find
     *
     * @param nameLike nameLike
     * @param pageable pageable
     * @return page
     */
    Page<ExtModel> find(String nameLike, Pageable pageable);

    /**
     * find
     *
     * @param name name
     * @return extModel
     */
    ExtModel find(String name);

    /**
     * delete
     *
     * @param name name
     */
    void delete(String name);

    /**
     * save
     *
     * @param name name
     * @param ext  ext
     * @return extModel
     */
    ExtModel save(String name, JsonNode ext);
}
