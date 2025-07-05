package com.zjjqtech.bimplatform.model.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.zjjqtech.bimplatform.model.Tag;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zao
 * @date 2020/09/24
 */
public class StrsToTagsDeserializer extends JsonDeserializer<List<Tag>> {

    private static final TypeReference<List<JsonNode>> LIST = new TypeReference<List<JsonNode>>() {};

    @Override
    public List<Tag> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (null != p) {
            List<JsonNode> list = p.readValueAs(LIST);
            return null == list ? null : list.stream().map(Tag::new).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
