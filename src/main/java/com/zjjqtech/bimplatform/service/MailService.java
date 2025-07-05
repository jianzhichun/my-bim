package com.zjjqtech.bimplatform.service;

import javax.mail.MessagingException;
import java.util.Locale;
import java.util.Map;

/**
 * @author zao
 * @date 2020/09/25
 */
public interface MailService {
    /**
     * send
     *
     * @param templateId templateId
     * @param to         to
     * @param variables  variables
     * @param locale     locale
     * @throws MessagingException MessagingException
     */
    void send(String templateId, String to, Map<String, Object> variables, Locale locale) throws MessagingException;
}
