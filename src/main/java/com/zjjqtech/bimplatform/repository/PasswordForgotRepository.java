package com.zjjqtech.bimplatform.repository;

import com.zjjqtech.bimplatform.model.PasswordForgot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zao
 * @date 2020/09/24
 */
public interface PasswordForgotRepository extends JpaRepository<PasswordForgot, String>, JpaSpecificationExecutor<PasswordForgot> {

    /**
     * findFirstByUserId
     *
     * @param accountId accountId
     * @return passwordForget
     */

    PasswordForgot findFirstByAccount_Id(String accountId);

}
