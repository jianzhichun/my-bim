package com.zjjqtech.bimplatform.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zjjqtech.bimplatform.model.utils.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

import static com.zjjqtech.bimplatform.security.SecurityConfig.getUser;

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
public class BimProject extends AbstractModel {

    private static final String NAME_PATTERN = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\s\\u4e00-\\u9fa5]{3,128}$";

    @Column(length = 128)
    @Length(min = 3, max = 128, message = "validate.error.bim-project.name.length")
    @Pattern(regexp = NAME_PATTERN, message = "validate.error.bim-project.name.pattern")
    private String name;
    @ManyToMany
    @JsonSerialize(using = UserToUserAbbr.class)
    private List<User> owners;
    @JsonSerialize(using = TagsToStrsSerializer.class)
    @JsonDeserialize(using = StrsToTagsDeserializer.class)
    @ManyToMany
    private List<Tag> tags;
    @Column(columnDefinition = "json")
    @Convert(converter = BimModelListConverter.class)
    private List<BimModel> models = new ArrayList<>();
    private boolean publicShare;
    private String shareToken;

    public BimProject() {
    }

    public BimProject(String bimId) {
        this.id = bimId;
    }

    @PrePersist
    public void prePersist() {
        if (CollectionUtils.isEmpty(owners)) {
            owners = new ArrayList<>();
            getUser().ifPresent(owners::add);
        }
    }

    public BimProject merge(BimProject other) {
        if (null != other.name) {
            this.name = other.name;
        }
        if (null != other.owners) {
            this.owners = other.owners;
        }
        if (null != other.tags) {
            this.tags = other.tags;
        }
        if (null != other.ext) {
            this.ext = other.ext;
        }
        return this;
    }
}
