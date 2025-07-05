package com.zjjqtech.bimplatform.security;

import com.zjjqtech.bimplatform.controller.utils.Result;
import com.zjjqtech.bimplatform.model.Authority;
import com.zjjqtech.bimplatform.model.User;
import com.zjjqtech.bimplatform.repository.AuthorityRepository;
import com.zjjqtech.bimplatform.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Optional;

import static com.zjjqtech.bimplatform.infrastructure.spring.SpringContextAware.*;

/**
 * @author zao
 * @date //
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String USER = "USER", ADMIN = "ADMIN", USER_ROLE = "ROLE_" + USER, ADMIN_ROLE = "ROLE_" + ADMIN, APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
    public static Authority userAuthority, adminAuthority;

    private final UserRepository userRepository;
    private final DataSource dataSource;
    private final AuthorityRepository authorityRepository;

    public SecurityConfig(UserRepository userRepository, DataSource dataSource, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.dataSource = dataSource;
        this.authorityRepository = authorityRepository;
    }

    @PostConstruct
    public void postConstruct() {
        try {
            userAuthority = this.authorityRepository.findFirstByAuthority(USER_ROLE);
            if (null == userAuthority) {
                userAuthority = this.authorityRepository.saveAndFlush(new Authority(USER_ROLE));
            }
            adminAuthority = this.authorityRepository.findFirstByAuthority(ADMIN_ROLE);
            if (null == adminAuthority) {
                adminAuthority = this.authorityRepository.saveAndFlush(new Authority(ADMIN_ROLE));
            }
            final String adminEmail = $env("admin-email", "zzchun12826@gmail.com");
            User admin = this.userRepository.findFirstByName(adminEmail);
            if (null == admin) {
                String password = "";
                while (!password.matches(User.PASSWORD_PATTERN)) {
                    password = RandomStringUtils.randomAlphanumeric(7) + RandomStringUtils.randomGraph(1);
                }
                if (log.isDebugEnabled()) {
                    log.debug("Admin: {} password: {}", adminEmail, password);
                }
                admin = new User();
                admin.setUsername(ADMIN.toLowerCase());
                admin.setEmail(adminEmail);
                admin.setPassword(password);
                admin.setAuthorities(Collections.singletonList(adminAuthority));
                this.userRepository.save(admin);
            }
        } catch (Exception e) {
            log.warn("SecurityConfig postConstruct error, ", e);
        }
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        try (Connection conn = dataSource.getConnection(); Statement stat = conn.createStatement()) {
            stat.execute("create table if not exists persistent_logins (username varchar(64) not null, series varchar(64) primary key, token varchar(64) not null, last_used timestamp not null);");
            stat.execute("CREATE TABLE IF NOT EXISTS SPRING_SESSION (\n" +
                    "\tPRIMARY_ID CHAR(36) NOT NULL,\n" +
                    "\tSESSION_ID CHAR(36) NOT NULL,\n" +
                    "\tCREATION_TIME BIGINT NOT NULL,\n" +
                    "\tLAST_ACCESS_TIME BIGINT NOT NULL,\n" +
                    "\tMAX_INACTIVE_INTERVAL INT NOT NULL,\n" +
                    "\tEXPIRY_TIME BIGINT NOT NULL,\n" +
                    "\tPRINCIPAL_NAME VARCHAR(100),\n" +
                    "\tCONSTRAINT SPRING_SESSION_PK PRIMARY KEY (PRIMARY_ID)\n," +
                    "UNIQUE INDEX SPRING_SESSION_IX1(SESSION_ID)\n," +
                    "INDEX SPRING_SESSION_IX2(EXPIRY_TIME)\n," +
                    "INDEX SPRING_SESSION_IX3(PRINCIPAL_NAME)" +
                    ") ENGINE=InnoDB ROW_FORMAT=DYNAMIC;");
            stat.execute("CREATE TABLE IF NOT EXISTS SPRING_SESSION_ATTRIBUTES (\n" +
                    "\tSESSION_PRIMARY_ID CHAR(36) NOT NULL,\n" +
                    "\tATTRIBUTE_NAME VARCHAR(200) NOT NULL,\n" +
                    "\tATTRIBUTE_BYTES BLOB NOT NULL,\n" +
                    "\tCONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),\n" +
                    "\tCONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE\n" +
                    ") ENGINE=InnoDB ROW_FORMAT=DYNAMIC;");
        } catch (SQLException ignored) {
        }
        return jdbcTokenRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String success = $json().writeValueAsString(Result.of(null));
        http.exceptionHandling()
                .authenticationEntryPoint(((request, response, authException) -> {
                    response.setContentType(APPLICATION_JSON_CHARSET_UTF_8);
                    response.getWriter().write($json().writeValueAsString(Result.fail($t(authException.getMessage()))));
                }))
                .accessDeniedHandler(((request, response, accessDeniedException) -> {
                    response.setContentType(APPLICATION_JSON_CHARSET_UTF_8);
                    String message;
                    if (accessDeniedException instanceof MissingCsrfTokenException) {
                        message = "csrf-token.missing";
                    } else {
                        message = accessDeniedException.getMessage();
                    }
                    response.getWriter().write($json().writeValueAsString(Result.fail($t(message))));
                }))
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringAntMatchers("/druid/*")
                .and()
                .authorizeRequests()
                .antMatchers("/api/user/login", "/api/user/logout", "/api/user/self", "/api/user/signUp", "/api/user/*/forgotPassword", "/api/user/*/resetPassword", "/api/user/*/*/resetPassword")
                .permitAll()
                .antMatchers(HttpMethod.GET)
                .permitAll()
                .antMatchers("/druid/*")
                .hasAnyRole(ADMIN)
                .anyRequest()
                .hasAnyRole(USER, ADMIN)
                .and()
                .formLogin()
                .loginProcessingUrl("/api/user/login")
                .successHandler(((request, response, authentication) -> {
                    response.setContentType(APPLICATION_JSON_CHARSET_UTF_8);
                    response.getWriter().write(success);
                }))
                .failureHandler(((request, response, exception) -> {
                    response.setContentType(APPLICATION_JSON_CHARSET_UTF_8);
                    response.getWriter().write($json().writeValueAsString(Result.fail($t("validate.error.login.name-pass"))));
                }))
                .and()
                .logout()
                .logoutUrl("/api/user/logout")
                .logoutSuccessHandler(((request, response, authentication) -> {
                    response.setContentType(APPLICATION_JSON_CHARSET_UTF_8);
                    response.getWriter().write(success);
                }))
                .invalidateHttpSession(true)
                .and()
                .rememberMe()
                .rememberMeParameter("remember")
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds($env("remember-me.token-validity-seconds", Integer.class, 604800));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userRepository::findFirstByName).passwordEncoder(passwordEncoder());
    }

    public static Optional<User> getUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .filter(User.class::isInstance)
                .map(User.class::cast);
    }
}
