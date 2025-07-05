package com.zjjqtech.bimplatform.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author zao
 * @date 2020/09/21
 */
@EqualsAndHashCode(callSuper = true)
@Audited
@DynamicInsert
@DynamicUpdate
@Entity
@Data
public class Authority extends AbstractModel implements GrantedAuthority {

    @Column(unique = true)
    @Length(min = 1, max = 255, message = "validate.error.authority.length")
    private String authority;

    public Authority() {
    }

    public Authority(String authority) {
        this.authority = authority;
    }
}
