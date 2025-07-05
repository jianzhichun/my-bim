package com.zjjqtech.bimplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.zjjqtech.bimplatform.model.utils.JsonConverter;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zao
 * @date 2020/09/21
 */
@Audited
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdOn", "updatedOn", "createdBy", "updatedBy"}, allowGetters = true)
public abstract class AbstractModel implements Serializable {

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
    protected String id;
    @CreatedDate
    protected LocalDateTime createdOn;
    @LastModifiedDate
    protected LocalDateTime updatedOn;
    @CreatedBy
    @Column(length = 32)
    protected String createdBy;
    @LastModifiedBy
    @Column(length = 32)
    protected String updatedBy;
    @Column(columnDefinition = "json")
    @Convert(converter = JsonConverter.class)
    protected JsonNode ext;

}
