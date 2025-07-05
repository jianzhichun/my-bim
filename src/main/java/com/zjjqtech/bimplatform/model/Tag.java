package com.zjjqtech.bimplatform.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Pattern;

/**
 * @author zao
 * @date 2020/09/23
 */
@EqualsAndHashCode(callSuper = true)
@Audited
@DynamicInsert
@DynamicUpdate
@Entity
@Data
public class Tag extends AbstractModel {

    private static final String NAME_PATTERN = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]{1,128}$";

    @Column(length = 128, unique = true)
    @Length(min = 1, max = 128, message = "validate.error.tag.name.length")
    @Pattern(regexp = NAME_PATTERN, message = "validate.error.tag.name.pattern")
    private String name;

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public Tag(JsonNode node) {
        if (node.isTextual()) {
            this.name = node.asText();
        } else {
            this.id = node.get("id").asText();
            this.name = node.get("name").asText();
        }
    }
}
