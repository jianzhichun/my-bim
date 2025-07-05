package com.zjjqtech.bimplatform.service;

import com.zjjqtech.bimplatform.model.Tag;
import com.zjjqtech.bimplatform.model.TagAbbr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author zao
 * @date 2020/09/24
 */
public interface TagService {

    /**
     * find
     *
     * @param nameLike nameLike
     * @param pageable pageable
     * @return page
     */
    Page<TagAbbr> find(String nameLike, Pageable pageable);

    /**
     * findByProjectIdLimit3
     *
     * @param projectId projectId
     * @return tags
     */
    List<TagAbbr> findByProjectIdLimit3(String projectId);

    /**
     * find
     *
     * @param name name
     * @return tag
     */
    Tag find(String name);

    /**
     * delete
     *
     * @param name name
     */
    void delete(String name);
}
