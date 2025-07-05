package com.zjjqtech.bimplatform.controller;

import com.zjjqtech.bimplatform.model.Authority;
import com.zjjqtech.bimplatform.model.PasswordForgot;
import com.zjjqtech.bimplatform.model.User;
import com.zjjqtech.bimplatform.model.UserAbbr;
import com.zjjqtech.bimplatform.service.MailService;
import com.zjjqtech.bimplatform.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.zjjqtech.bimplatform.security.SecurityConfig.adminAuthority;

/**
 * @author zao
 * @date 2020/09/21
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final MailService mailService;

    public UserController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @GetMapping("/self")
    public User self(@AuthenticationPrincipal @ApiIgnore User self) {
        return self;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{name}")
    public User find(@PathVariable String name) {
        return userService.find(name);
    }

    @PreAuthorize("hasRole('ADMIN') || @userService.checkIsOwnerOfUser(#id)")
    @PutMapping("/{id}")
    public User update(@PathVariable String id, @RequestBody User user, @AuthenticationPrincipal(expression = "authorities") @ApiIgnore List<Authority> authorities) {
        user.setId(id);
        user.setPassword(null);
        if (authorities.stream().map(Authority::getId).noneMatch(adminAuthority.getId()::equals)) {
            user.setAuthorities(user.getAuthorities().stream().filter(a -> adminAuthority.getId().equals(a.getId())).collect(Collectors.toList()));
        }
        return userService.save(user);
    }

    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "Results page you want to retrieve (0..N)"),
                    @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query", value = "Number of records per page."),
                    @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                            value = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.")
            }
    )
    @GetMapping
    public Page<UserAbbr> find(String nameLike, @ApiIgnore Pageable pageable) {
        return userService.find(nameLike, pageable);
    }

    @PostMapping("/signUp")
    public User signUp(String username, String email, String phone, String password, String confirmedPassword) {
        return userService.signUp(username, email, phone, password, confirmedPassword);
    }

    @PostMapping("/{name}/resetPassword")
    public void resetPasswordByOldPassword(@PathVariable String name, String password, String confirmedPassword, String oldPassword) {
        userService.resetPasswordByOldPassword(password, confirmedPassword, name, oldPassword);
    }

    @PostMapping("/{userId}/{sid}/resetPassword")
    public void resetPasswordByForgetPasswordSid(@PathVariable String userId, @PathVariable String sid, String password, String confirmedPassword) {
        userService.resetPasswordByForgetPasswordSid(password, confirmedPassword, userId, sid);
    }

    @PostMapping("/{name}/forgotPassword")
    public void forgotPassword(@PathVariable String name, HttpServletRequest request) throws MessagingException {
        PasswordForgot passwordForgot = userService.saveForgetPasswordByName(name);
        User account = passwordForgot.getAccount();
        mailService.send("forget-password", account.getEmail(), new HashMap<String, Object>(5) {{
            put("username", account.getUsername());
            put("link", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +
                    "/resetPassword?sid=" + passwordForgot.getSid() + "&userId=" + account.getId());
        }}, LocaleContextHolder.getLocale());
    }
}
