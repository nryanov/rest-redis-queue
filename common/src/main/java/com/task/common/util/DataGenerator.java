package com.task.common.util;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class DataGenerator {
    private static int KB_200 = 200 * 1024;

    private Random random = new Random();

    public byte[] generate() {
        byte[] data = new byte[KB_200];
        random.nextBytes(data);
        return data;
    }

    public String randomQueueName() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
