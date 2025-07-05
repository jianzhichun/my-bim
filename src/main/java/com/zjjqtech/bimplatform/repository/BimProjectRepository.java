package com.zjjqtech.bimplatform.repository;

import com.zjjqtech.bimplatform.model.BimProject;
import com.zjjqtech.bimplatform.model.BimProjectAbbr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author zao
 * @date 2020/09/21
 */
public interface BimProjectRepository extends JpaRepository<BimProject, String>, JpaSpecificationExecutor<BimProject> {

    /**
     * findFirstByName
     *
     * @param name name
     * @return bimProject
     */
    @Query("select b from BimProject b where b.name = :name or b.id = :name")
    BimProject findFirstByName(String name);

    /**
     * existsByIdAndOwners
     *
     * @param id       id
     * @param ownersId ownersId
     * @return existed
     */
    boolean existsByIdAndOwners_id(String id, String ownersId);

    /**
     * existsByIdAndShareToken
     *
     * @param id         id
     * @param shareToken shareToken
     * @return existed
     */
    boolean existsByIdAndShareToken(String id, String shareToken);

    /**
     * existsByIdAndPublicShareIsTrue
     *
     * @param id id
     * @return existed
     */
    boolean existsByIdAndPublicShareIsTrue(String id);

    /**
     * findByNameLikeOrderByUpdatedOnDesc
     *
     * @param nameLike nameLike
     * @param tags     tags
     * @param pageable pageable
     * @return page
     */
    Page<BimProjectAbbr> findByNameLikeAndTags_NameIn(String nameLike, List<String> tags, Pageable pageable);

    /**
     * findByNameLikeAndOwnersIsOrderByUpdatedOnDesc
     *
     * @param nameLike nameLike
     * @param tags     tags
     * @param ownerId  ownerId
     * @param pageable pageable
     * @return page
     */
    Page<BimProjectAbbr> findByNameLikeAndTags_NameInAndOwners_Id(String nameLike, List<String> tags, String ownerId, Pageable pageable);

    /**
     * findByNameLikeOrderByUpdatedOnDesc
     *
     * @param nameLike nameLike
     * @param pageable pageable
     * @return page
     */
    Page<BimProjectAbbr> findByNameLike(String nameLike, Pageable pageable);

    /**
     * findByNameLikeAndOwnersIsOrderByUpdatedOnDesc
     *
     * @param nameLike nameLike
     * @param ownerId  ownerId
     * @param pageable pageable
     * @return page
     */
    Page<BimProjectAbbr> findByNameLikeAndOwners_Id(String nameLike, String ownerId, Pageable pageable);
}
