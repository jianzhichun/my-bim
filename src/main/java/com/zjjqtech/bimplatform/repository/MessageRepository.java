package com.zjjqtech.bimplatform.repository;

import com.zjjqtech.bimplatform.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zao
 * @date 2020/09/25
 */
public interface MessageRepository extends JpaRepository<Message, String>, JpaSpecificationExecutor<Message> {

    /**
     * findFirstByKeyAndLocale
     *
     * @param locale locale
     * @param key    key
     * @return message
     */
    Message findFirstByLocaleAndKey(String locale, String key);

    /**
     * findByLocaleAndKeyLike
     *
     * @param locale   locale
     * @param keyLike  keyLike
     * @param pageable pageable
     * @return page
     */
    Page<Message> findByLocaleAndKeyLike(String locale, String keyLike, Pageable pageable);
}
