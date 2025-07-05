package com.zjjqtech.bimplatform.repository;

import com.zjjqtech.bimplatform.model.User;
import com.zjjqtech.bimplatform.model.UserAbbr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author zao
 * @date 2020/09/21
 */
@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    /**
     * findFirstByName
     *
     * @param name name
     * @return user
     */
    @EntityGraph(value = "user.withAuthorities", type = EntityGraph.EntityGraphType.FETCH)
    @Query("select u from User u where u.username = :name or u.email = :name or u.phone = :name or u.id = :name")
    User findFirstByName(String name);

    /**
     * findByUsernameLike
     *
     * @param nameLike nameLike
     * @param pageable pageable
     * @return page
     */
    Page<UserAbbr> findByUsernameLike(String nameLike, Pageable pageable);
}
