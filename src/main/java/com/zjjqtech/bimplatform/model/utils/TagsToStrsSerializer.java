package com.zjjqtech.bimplatform.model.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zjjqtech.bimplatform.model.Tag;

import java.io.IOException;
import java.util.List;

/**
 * @author zao
 * @date 2020/09/23
 */
public class TagsToStrsSerializer extends JsonSerializer<List<Tag>> {

    @Override
    public void serialize(List<Tag> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String[] array = value.stream().map(Tag::getName).toArray(String[]::new);
        gen.writeArray(array, 0, array.length);
    }
}
