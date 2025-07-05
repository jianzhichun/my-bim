package com.zjjqtech.bimplatform.service.impl;

import com.zjjqtech.bimplatform.model.Message;
import com.zjjqtech.bimplatform.repository.MessageRepository;
import com.zjjqtech.bimplatform.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author zao
 * @date 2020/09/25
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Page<Message> find(String locale, String nameLike, Pageable pageable) {
        return this.messageRepository.findByLocaleAndKeyLike(locale, nameLike, pageable);
    }

    @Override
    public Message find(String locale, String key) {
        return this.messageRepository.findFirstByLocaleAndKey(locale, key);
    }

    @Override
    public Message save(String locale, String key, String content) {
        Message message = this.messageRepository.findFirstByLocaleAndKey(locale, key);
        if (null == message) {
            message = new Message();
            message.setLocale(locale);
            message.setKey(key);
            message.setContent(content);
        }
        return this.messageRepository.save(message);
    }

    @Override
    public void delete(String locale, String key) {
        Message message = this.messageRepository.findFirstByLocaleAndKey(locale, key);
        if (null != message) {
            this.messageRepository.delete(message);
        }
    }

}
