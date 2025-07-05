package com.zjjqtech.bimplatform.repository;

import com.zjjqtech.bimplatform.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zao
 * @date 2020/09/22
 */
public interface AuthorityRepository extends JpaRepository<Authority, String>, JpaSpecificationExecutor<Authority> {

    /**
     * existsByAuthorityEquals
     *
     * @param authority authority
     * @return existed
     */
    boolean existsByAuthority(String authority);

    /**
     * findFirstByAuthority
     *
     * @param authority authority
     * @return authority
     */
    Authority findFirstByAuthority(String authority);
}
