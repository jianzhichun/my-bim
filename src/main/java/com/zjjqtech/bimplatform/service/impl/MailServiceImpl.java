package com.zjjqtech.bimplatform.service.impl;

import com.zjjqtech.bimplatform.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Map;

import static com.zjjqtech.bimplatform.infrastructure.spring.SpringContextAware.$env;
import static com.zjjqtech.bimplatform.infrastructure.spring.SpringContextAware.$t;

/**
 * @author zao
 * @date 2020/09/25
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    public MailServiceImpl(SpringTemplateEngine templateEngine, MessageSource messageSource, JavaMailSender javaMailSender) {
        templateEngine.setMessageSource(messageSource);
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Async
    public void send(String templateId, String to, Map<String, Object> variables, Locale locale) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setFrom($env("spring.mail.username"));
        messageHelper.setTo(to);
        messageHelper.setSubject($t("template.mail.forget-password.title", locale));
        Context context = new Context();
        context.setLocale(locale);
        context.setVariables(variables);
        String html = templateEngine.process(templateId, context);
        messageHelper.setText(html, true);
        javaMailSender.send(mimeMessage);
    }
}
