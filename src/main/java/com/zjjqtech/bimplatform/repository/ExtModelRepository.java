package com.zjjqtech.bimplatform.repository;

import com.zjjqtech.bimplatform.model.ExtModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zao
 * @date 2020/09/24
 */
public interface ExtModelRepository extends JpaRepository<ExtModel, String>, JpaSpecificationExecutor<ExtModelRepository> {

    /**
     * findByNameLike
     *
     * @param nameLike nameLike
     * @param pageable pageable
     * @return page
     */
    Page<ExtModel> findByNameLike(String nameLike, Pageable pageable);

    /**
     * findFirstByName
     *
     * @param name name
     * @return ext
     */
    ExtModel findFirstByName(String name);

    /**
     * deleteByName
     *
     * @param name name
     */
    void deleteByName(String name);
}
