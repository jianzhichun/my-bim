package com.zjjqtech.bimplatform.service;

import com.zjjqtech.bimplatform.model.PasswordForgot;
import com.zjjqtech.bimplatform.model.User;
import com.zjjqtech.bimplatform.model.UserAbbr;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.zjjqtech.bimplatform.model.User.*;

/**
 * @author zao
 * @date 2020/09/21
 */
@Validated
public interface UserService {

    /**
     * findUser
     *
     * @param userName userName
     * @return user
     */
    User find(String userName);

    /**
     * signUp
     *
     * @param username          username
     * @param email             email
     * @param phone             phone
     * @param password          password
     * @param confirmedPassword confirmedPassword
     * @return user
     */
    User signUp(
            @NotEmpty(message = "validate.error.user.username.not-empty")
            @Pattern(regexp = NAME_PATTERN, message = "validate.error.user.username.pattern")
            @Length(min = 5, max = 32, message = "validate.error.user.username.length")
                    String username,
            @Length(max = 128, message = "validate.error.user.email.length")
            @Pattern(regexp = EMAIL_PATTERN, message = "validate.error.user.email.pattern")
                    String email,
            @Pattern(regexp = PHONE_PATTERNS, message = "validate.error.user.phone.pattern")
                    String phone,
            @NotEmpty(message = "validate.error.user.password.not-empty")
            @Length(min = 8, max = 32, message = "validate.error.user.password.length")
            @Pattern(regexp = PASSWORD_PATTERN, message = "validate.error.user.password.pattern")
                    String password,
            String confirmedPassword
    );

    /**
     * update
     *
     * @param user user
     * @return user
     */
    User save(@Valid User user);

    /**
     * find
     *
     * @param nameLike nameLike
     * @param pageable pageable
     * @return page
     */
    Page<UserAbbr> find(String nameLike, Pageable pageable);

    /**
     * updatePasswordByOldPassword
     *
     * @param password          password
     * @param confirmedPassword confirmedPassword
     * @param name              name
     * @param oldPassword       oldPassword
     */
    void resetPasswordByOldPassword(
            @NotEmpty(message = "validate.error.user.password.not-empty")
            @Length(min = 8, max = 32, message = "validate.error.user.password.length")
            @Pattern(regexp = PASSWORD_PATTERN, message = "validate.error.user.password.pattern")
                    String password,
            String confirmedPassword,
            String name,
            String oldPassword
    );

    /**
     * updatePasswordByForgetPasswordSid
     *
     * @param password          password
     * @param confirmedPassword confirmedPassword
     * @param userId            userId
     * @param sid               sid
     */
    void resetPasswordByForgetPasswordSid(
            @NotEmpty(message = "validate.error.user.password.not-empty")
            @Length(min = 8, max = 32, message = "validate.error.user.password.length")
            @Pattern(regexp = PASSWORD_PATTERN, message = "validate.error.user.password.pattern")
                    String password,
            String confirmedPassword,
            String userId,
            String sid
    );

    /**
     * forgetPasswordByName
     *
     * @param name name
     * @return passwordForget
     */
    PasswordForgot saveForgetPasswordByName(String name);

    /**
     * checkIsOwnerOfUser
     *
     * @param userId userId
     * @return is or not
     */
    boolean checkIsOwnerOfUser(String userId);
}
