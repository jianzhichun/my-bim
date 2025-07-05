package com.zjjqtech.bimplatform.model.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zjjqtech.bimplatform.model.User;
import com.zjjqtech.bimplatform.model.UserAbbr;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class UserToUserAbbr extends JsonSerializer<List<User>> {
    @Override
    public void serialize(List<User> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value.stream().map(user -> new UserAbbr() {
            @Override
            public String getId() {
                return user.getId();
            }

            @Override
            public String getUsername() {
                return user.getUsername();
            }
        }).collect(Collectors.toList()));
    }
}
