package com.zjjqtech.bimplatform.model.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zjjqtech.bimplatform.model.BimModel;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.util.List;

import static com.zjjqtech.bimplatform.infrastructure.spring.SpringContextAware.$json;

@Slf4j
public class BimModelListConverter implements AttributeConverter<List<BimModel>, String> {
    private static final TypeReference<List<BimModel>> LIST = new TypeReference<List<BimModel>>() {
    };

    @Override
    public String convertToDatabaseColumn(List<BimModel> attribute) {
        try {
            return null == attribute ? null : $json().writeValueAsString(attribute);
        } catch (Exception e) {
            log.error("writeValueAsString error,", e);
            return null;
        }
    }

    @Override
    public List<BimModel> convertToEntityAttribute(String dbData) {
        try {
            return null == dbData ? null : $json().readValue(dbData, LIST);
        } catch (Exception e) {
            log.error("readValue error,", e);
            return null;
        }
    }
}
