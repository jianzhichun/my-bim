package com.zjjqtech.bimplatform.infrastructure.mvc;

import com.zjjqtech.bimplatform.model.Message;
import com.zjjqtech.bimplatform.repository.MessageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * @author zao
 * @date 2020/09/25
 */
@Configuration
public class WebMvcConfigurerAdapter implements WebMvcConfigurer {

    public static final String LANG = "lang", MESSAGES = "messages", UTF_8 = "utf8";

    @Bean
    public MessageSource messageSource(MessageRepository messageRepository) {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasenames(MESSAGES);
        resourceBundleMessageSource.setDefaultEncoding(UTF_8);
        resourceBundleMessageSource.setParentMessageSource(new AbstractMessageSource() {

            @Override
            protected MessageFormat resolveCode(String code, Locale locale) {
                Message message = messageRepository.findFirstByLocaleAndKey(locale.getLanguage(), code);
                return new MessageFormat(null == message ? code : message.getContent(), locale);
            }
        });
        return resourceBundleMessageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.CHINA);
        return resolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName(LANG);
        registry.addInterceptor(localeChangeInterceptor);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldType(String.class, new Formatter<String>() {
            @Override
            public String print(String object, Locale locale) {
                return object;
            }

            @Override
            public String parse(String text, Locale locale) {
                if (StringUtils.isBlank(text)) {
                    return null;
                }
                return text;
            }
        });
    }
}
