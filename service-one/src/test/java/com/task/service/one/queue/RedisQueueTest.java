package com.task.service.one.queue;

import com.task.common.model.QueueInfo;
import com.task.common.util.DataGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.test.StepVerifier;

import java.util.Arrays;

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
    @Qualifier("signed")
    private ReactiveRedisTemplate<String, byte[]> signedTemplate;
    @Autowired
    @Qualifier("unsigned")
    private ReactiveRedisTemplate<String, byte[]> unsignedTemplate;

    @Test
    public void test() {
        byte[] unsignedData = new byte[] {1};
        QueueInfo queueInfo = new QueueInfo(generator.randomQueueName(), generator.randomQueueName());

        StepVerifier.create(redisQueue.send(queueInfo, unsignedData)).expectNextCount(1).expectComplete().verify();
        StepVerifier.create(unsignedTemplate.opsForList().size(queueInfo.getUnsignedDataQueue())).expectNext(1L).expectComplete().verify();

        byte[] saved = unsignedTemplate.opsForList().leftPop(queueInfo.getUnsignedDataQueue()).block();
        assertArrayEquals(unsignedData, saved);

        byte[] expected = new byte[] {2};
        signedTemplate.opsForList().leftPush(queueInfo.getSignatureQueue(), expected).block();

        StepVerifier.create(redisQueue.receive(queueInfo)).expectNextMatches(next -> Arrays.equals(next, expected)).expectComplete().verify();
    }
}
