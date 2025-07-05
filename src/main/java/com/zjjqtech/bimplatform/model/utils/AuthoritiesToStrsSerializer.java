package com.zjjqtech.bimplatform.model.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zjjqtech.bimplatform.model.Authority;

import java.io.IOException;
import java.util.List;

/**
 * @author zao
 * @date 2020/09/22
 */
public class AuthoritiesToStrsSerializer extends JsonSerializer<List<Authority>> {

    @Override
    public void serialize(List<Authority> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String[] array = value.stream().map(Authority::getAuthority).toArray(String[]::new);
        gen.writeArray(array, 0, array.length);
    }
}
