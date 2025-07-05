package com.zjjqtech.bimplatform.service.impl;

import com.zjjqtech.bimplatform.infrastructure.exception.BizException;
import com.zjjqtech.bimplatform.model.Authority;
import com.zjjqtech.bimplatform.model.PasswordForgot;
import com.zjjqtech.bimplatform.model.User;
import com.zjjqtech.bimplatform.model.UserAbbr;
import com.zjjqtech.bimplatform.repository.AuthorityRepository;
import com.zjjqtech.bimplatform.repository.PasswordForgotRepository;
import com.zjjqtech.bimplatform.repository.UserRepository;
import com.zjjqtech.bimplatform.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

import static com.zjjqtech.bimplatform.infrastructure.spring.SpringContextAware.$bean;
import static com.zjjqtech.bimplatform.infrastructure.spring.SpringContextAware.$env;
import static com.zjjqtech.bimplatform.security.SecurityConfig.getUser;
import static com.zjjqtech.bimplatform.security.SecurityConfig.userAuthority;

/**
 * @author zao
 * @date 2020/09/21
 */
@Service("userService")
@Transactional(rollbackOn = Exception.class)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordForgotRepository passwordForgotRepository;
    private final AuthorityRepository authorityRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordForgotRepository passwordForgotRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.passwordForgotRepository = passwordForgotRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public User find(String userName) {
        return userRepository.findFirstByName(userName);
    }

    @Override
    public User signUp(String username, String email, String phone, String password, String confirmedPassword) {
        if (null == email && null == phone) {
            throw new BizException("validate.error.user.email-phone.not-null");
        }
        if (!Objects.equals(password, confirmedPassword)) {
            throw new BizException("validate.error.user.unequal-confirmed-password");
        }
        if (null != userRepository.findFirstByName(username) || null != userRepository.findFirstByName(email) || null != userRepository.findFirstByName(phone)) {
            throw new BizException("validate.error.user.existed");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);
        user.setAuthorities(Collections.singletonList(userAuthority));
        checkAuthority(user);
        return userRepository.save(user);
    }

    @Override
    public User save(User user) {
        checkAuthority(user);
        return userRepository.save(user);
    }

    @Override
    public Page<UserAbbr> find(String nameLike, Pageable pageable) {
        return userRepository.findByUsernameLike(nameLike, pageable);
    }

    @Override
    public void resetPasswordByOldPassword(String password, String confirmedPassword, String name, String oldPassword) {
        if (!Objects.equals(password, confirmedPassword)) {
            throw new BizException("validate.error.user.unequal-confirmed-password");
        } else if (Objects.equals(password, oldPassword)) {
            throw new BizException("validate.error.user.equals-old-password-password");
        }
        User user = userRepository.findFirstByName(name);
        if (null != user) {
            if (!$bean(PasswordEncoder.class).matches(oldPassword, user.getPassword())) {
                throw new BizException("validate.error.user.invalid-old-password");
            } else {
                user.setPassword(password);
                userRepository.save(user);
            }
        } else {
            throw new BizException("validate.error.user.non-existed");
        }
    }

    @Override
    public void resetPasswordByForgetPasswordSid(String password, String confirmedPassword, String userId, String sid) {
        if (!Objects.equals(password, confirmedPassword)) {
            throw new BizException("validate.error.user.unequal-confirmed-password");
        }
        PasswordForgot passwordForgot = passwordForgotRepository.findFirstByAccount_Id(userId);
        if (null == passwordForgot) {
            throw new BizException("validate.error.forget-password.sid.non-existed");
        } else {
            User user = passwordForgot.getAccount();
            if (null == user) {
                throw new BizException("validate.error.user.non-existed");
            } else {
                if ($bean(PasswordEncoder.class).matches(password, user.getPassword())) {
                    throw new BizException("validate.error.user.equals-old-password-password");
                } else if (passwordForgot.getUpdatedOn().plusSeconds($env("password-forget.sid.expired-seconds", Long.class, 1800L)).isBefore(LocalDateTime.now())) {
                    throw new BizException("validate.error.forget-password.sid.expired");
                } else if (!Objects.equals(passwordForgot.getSid(), sid)) {
                    throw new BizException("validate.error.forget-password.sid.invalid");
                } else {
                    user.setPassword(password);
                    userRepository.save(user);
                    passwordForgotRepository.delete(passwordForgot);
                }
            }
        }
    }

    @Override
    public PasswordForgot saveForgetPasswordByName(String name) {
        User account = userRepository.findFirstByName(name);
        if (null == account) {
            throw new BizException("validate.error.user.non-existed");
        } else {
            String email = account.getEmail();
            if (null == email) {
                throw new BizException("validate.error.email.non-existed");
            } else {
                PasswordForgot passwordForgot = passwordForgotRepository.findFirstByAccount_Id(account.getId());
                if (null == passwordForgot) {
                    passwordForgot = new PasswordForgot();
                    passwordForgot.setAccount(account);
                }
                passwordForgot.setSid(null);
                return passwordForgotRepository.save(passwordForgot);
            }
        }
    }

    private void checkAuthority(User user) {
        if (!CollectionUtils.isEmpty(user.getAuthorities())) {
            for (Authority authority : user.getAuthorities()) {
                if (null != authority) {
                    if (StringUtils.isEmpty(authority.getId())) {
                        if (StringUtils.isEmpty(authority.getAuthority())) {
                            throw new BizException("validate.error.authority.empty");
                        } else {
                            Authority authority0 = authorityRepository.findFirstByAuthority(authority.getAuthority());
                            if (null == authority0) {
                                throw new BizException("validate.error.authority.unknown-authority");
                            } else {
                                authority0.setId(authority0.getId());
                            }
                        }
                    } else {
                        if (!authorityRepository.existsById(authority.getId())) {
                            throw new BizException("validate.error.authority.unknown-authority");
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean checkIsOwnerOfUser(String userId) {
        return getUser().map(User::getId).map(userId::equals).orElse(false);
    }

}
