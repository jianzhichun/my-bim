package com.zjjqtech.bimplatform.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zjjqtech.bimplatform.model.utils.AuthoritiesToStrsSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.zjjqtech.bimplatform.infrastructure.spring.SpringContextAware.$bean;

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
@NamedEntityGraph(name = "user.withAuthorities", attributeNodes = {@NamedAttributeNode("authorities")})
public class User extends AbstractModel implements UserDetails {


    public static final String NAME_PATTERN = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\\u4e00-\\u9fa5]{5,128}$",
            PASSWORD_PATTERN = "^(?![A-Za-z0-9]+$)(?![a-z0-9\\W]+$)(?![A-Za-z\\W]+$)(?![A-Z0-9\\W]+$)[a-zA-Z0-9\\W]{8,32}$",
            EMAIL_PATTERN = "^\\s*\\w+(?:\\.?[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$",
            PHONE_PATTERNS = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";

    @Column(length = 32, unique = true)
    @NotEmpty(message = "validate.error.user.username.not-empty")
    @Pattern(regexp = NAME_PATTERN, message = "validate.error.user.username.pattern")
    @Length(min = 5, max = 32, message = "validate.error.user.username.length")
    private String username;
    @Length(min = 8, max = 32, message = "validate.error.user.password.length")
    @Pattern(regexp = PASSWORD_PATTERN, message = "validate.error.user.password.pattern")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Column(length = 128, unique = true)
    @Length(max = 128, message = "validate.error.user.email.length")
    @Pattern(regexp = EMAIL_PATTERN, message = "validate.error.user.email.pattern")
    private String email;
    @Column(length = 32, unique = true)
    @Pattern(regexp = PHONE_PATTERNS, message = "validate.error.user.phone.pattern")
    private String phone;
    private boolean enabled = true;
    @JsonSerialize(using = AuthoritiesToStrsSerializer.class)
    @ManyToMany
    private List<Authority> authorities;
    @Transient
    private boolean accountNonExpired = true, accountNonLocked = true, credentialsNonExpired = true;

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    public User(String id, String username, String email, String phone, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.enabled = enabled;
    }

    public String getUsername() {
        return null == username ? (null == email ? (null == phone ? id : phone) : email) : username;
    }

    @PrePersist
    @PreUpdate
    public void prePersistAndPreUpdate() {
        if (null != password && password.matches(PASSWORD_PATTERN)) {
            this.password = $bean(PasswordEncoder.class).encode(this.password);
        }
    }

}
