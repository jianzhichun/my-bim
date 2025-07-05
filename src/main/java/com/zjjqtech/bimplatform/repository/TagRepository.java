package com.zjjqtech.bimplatform.repository;

import com.zjjqtech.bimplatform.model.Tag;
import com.zjjqtech.bimplatform.model.TagAbbr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author zao
 * @date 2020/09/23
 */
public interface TagRepository extends JpaRepository<Tag, String>, JpaSpecificationExecutor<Tag> {

    /**
     * findByNameLike
     *
     * @param nameLike nameLike
     * @param pageable pageable
     * @return page
     */
    Page<TagAbbr> findByNameLike(String nameLike, Pageable pageable);

    /**
     * findByProjectIdLimit3
     *
     * @param projectId projectId
     * @return tags
     */
    @Query(value = "select t.id id, t.name name from tag t inner join bim_project_tags p on t.id = p.tags_id where p.bim_project_id = :projectId limit 3", nativeQuery = true)
    List<TagAbbr> findByProjectIdLimit3(String projectId);

    /**
     * findFirstByName
     *
     * @param name name
     * @return tag
     */
    Tag findFirstByName(String name);

    /**
     * deleteByName
     *
     * @param name name
     */
    void deleteByName(String name);
}
