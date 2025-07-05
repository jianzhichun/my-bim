package com.zjjqtech.bimplatform.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.ZoneId;

/**
 * @author zao
 * @date 2020/09/24
 */
@EqualsAndHashCode(callSuper = true)
@DynamicInsert
@DynamicUpdate
@Entity
@Data
public class PasswordForgot extends AbstractModel {

    @OneToOne
    private User account;
    @Column(length = 32)
    private String sid;

    @PrePersist
    @PreUpdate
    public void prePersistAndPreUpdate() {
        this.sid = DigestUtils.md5Hex(String.join("&", account.getId(), RandomStringUtils.randomNumeric(6), String.valueOf(updatedOn.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())));
    }

}
