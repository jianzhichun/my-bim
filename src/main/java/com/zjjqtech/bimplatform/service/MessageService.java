package com.zjjqtech.bimplatform.service;

import com.zjjqtech.bimplatform.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author zao
 * @date 2020/09/25
 */
public interface MessageService {

    /**
     * find
     *
     * @param locale   locale
     * @param nameLike nameLike
     * @param pageable pageable
     * @return page
     */
    Page<Message> find(String locale, String nameLike, Pageable pageable);

    /**
     * find
     *
     * @param locale locale
     * @param key    key
     * @return message
     */
    Message find(String locale, String key);

    /**
     * save
     *
     * @param locale  locale
     * @param key     key
     * @param content content
     * @return message
     */
    Message save(String locale, String key, String content);

    /**
     * delete
     *
     * @param locale locale
     * @param key    key
     */
    void delete(String locale, String key);

}
