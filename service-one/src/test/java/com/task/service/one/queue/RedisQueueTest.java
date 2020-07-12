package com.task.service.one.queue;

import com.task.common.model.SignedData;
import com.task.common.model.UnsignedData;
import com.task.common.util.DataGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "queue.destination:topic",
        "queue.timeout:10",
})
public class RedisQueueTest extends AbstractRedisTest {
    @Autowired
    private DataGenerator generator;
    @Autowired
    private Queue redisQueue;
    @Autowired
    private ReactiveRedisTemplate<String, SignedData> signedTemplate;
    @Autowired
    private ReactiveRedisTemplate<String, UnsignedData> unsignedTemplate;

    @Test
    public void test() {
        UnsignedData unsignedData = UnsignedData.create(generator.randomQueueName(),generator.randomQueueName(), new byte[] {1});

        StepVerifier.create(redisQueue.send(unsignedData)).expectComplete().verify();
        StepVerifier.create(unsignedTemplate.opsForList().size(unsignedData.getFromQueue())).expectNext(1L).expectComplete().verify();

        UnsignedData saved = unsignedTemplate.opsForList().leftPop(unsignedData.getFromQueue()).block();
        assertEquals(unsignedData, saved);

        SignedData expected = SignedData.create(new byte[] {1}, new byte[] {1});
        signedTemplate.opsForList().leftPush(unsignedData.getToQueue(), expected).block();

        StepVerifier.create(redisQueue.receive(unsignedData.getToQueue())).expectNext(expected).expectComplete().verify();
    }
}
