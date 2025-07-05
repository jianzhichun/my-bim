package com.zjjqtech.bimplatform.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;

/**
 * @author zao
 * @date 2020/09/25
 */
@EqualsAndHashCode(callSuper = true)
@DynamicInsert
@DynamicUpdate
@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"locale", "message_key"}))
public class Message extends AbstractModel {

    @NotEmpty(message = "validate.error.message.locale.not-empty")
    @Length(min = 1, max = 10, message = "validate.error.message.locale.length")
    private String locale;
    @Column(name = "message_key")
    @NotEmpty(message = "validate.error.message.key.not-empty")
    @Length(min = 1, max = 255, message = "validate.error.message.key.length")
    private String key;
    @NotEmpty(message = "validate.error.message.content.not-empty")
    @Length(min = 1, max = 255, message = "validate.error.message.content.length")
    private String content;
}
