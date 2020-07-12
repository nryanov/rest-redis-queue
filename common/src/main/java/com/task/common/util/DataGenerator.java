package com.task.common.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class DataGenerator {
    //todo
//    private static int KB_200 = 200 * 1024 * 1024;
    private static int KB_200 = 1;
    private static int B_64 = 64;

    private Random random = new Random();

    public byte[] generate() {
        byte[] data = new byte[KB_200];
        random.nextBytes(data);
        return data;
    }

    public String randomQueueName() {
        byte[] data = new byte[B_64];
        random.nextBytes(data);
        return new String(data);
    }
}
