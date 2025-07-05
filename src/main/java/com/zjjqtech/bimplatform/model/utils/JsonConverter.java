package com.zjjqtech.bimplatform.model.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;

import static com.zjjqtech.bimplatform.infrastructure.spring.SpringContextAware.$json;

/**
 * @author zao
 * @date 2020/09/21
 */
@Slf4j
public class JsonConverter implements AttributeConverter<JsonNode, String> {

    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        return null == attribute ? null : attribute.toString();
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        try {
            return null == dbData ? null : $json().readTree(dbData);
        } catch (Exception e) {
            log.error("readValue error,", e);
            return null;
        }
    }
}
