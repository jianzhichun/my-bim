package com.zjjqtech.bimplatform.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Version;

/**
 * @author zao
 * @date 2020/09/24
 */
@EqualsAndHashCode(callSuper = true)
@Audited
@DynamicInsert
@DynamicUpdate
@Entity
@Data
public class ExtModel extends AbstractModel {

    @Column(length = 191, unique = true)
    @Length(min = 1, max = 191, message = "validate.error.ext-model.name.length")
    private String name;
    @Version
    private long version;
}
