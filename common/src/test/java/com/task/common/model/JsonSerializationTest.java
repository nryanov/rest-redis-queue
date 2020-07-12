package com.task.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// todo
@ExtendWith(SpringExtension.class)
public class JsonSerializationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void signedData() throws JsonProcessingException {
        SignedData signedData = SignedData.create(new byte[] {1}, new byte[] {1});
        String json = objectMapper.writeValueAsString(signedData);
        System.out.println(json);
    }

    @Test
    public void unsignedData() throws JsonProcessingException {
        UnsignedData unsignedData = UnsignedData.create("q1", "q2", new byte[] {1});
        String json = objectMapper.writeValueAsString(unsignedData);
        System.out.println(json);
    }
}
