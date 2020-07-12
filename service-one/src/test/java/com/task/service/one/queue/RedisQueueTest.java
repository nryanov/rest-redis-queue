package com.task.service.one.queue;

import com.task.common.model.QueueInfo;
import com.task.common.model.SignatureData;
import com.task.common.util.DataGenerator;
import com.task.service.one.AbstractRedisTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.test.StepVerifier;

import java.security.PublicKey;
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
    private ReactiveRedisTemplate<String, SignatureData> signedTemplate;
    @Autowired
    @Qualifier("unsigned")
    private ReactiveRedisTemplate<String, byte[]> unsignedTemplate;
    @Autowired
    private PublicKey publicKey;

    @Test
    public void test() {
        byte[] unsignedData = new byte[] {1};
        QueueInfo queueInfo = new QueueInfo(generator.randomQueueName(), generator.randomQueueName());

        StepVerifier.create(redisQueue.send(queueInfo, unsignedData)).expectNextCount(1).expectComplete().verify();
        StepVerifier.create(unsignedTemplate.opsForList().size(queueInfo.getUnsignedDataQueue())).expectNext(1L).expectComplete().verify();

        byte[] saved = unsignedTemplate.opsForList().leftPop(queueInfo.getUnsignedDataQueue()).block();
        assertArrayEquals(unsignedData, saved);

        byte[] expected = new byte[] {2};
        signedTemplate.opsForList().leftPush(queueInfo.getSignatureQueue(), SignatureData.create(expected, publicKey.getEncoded())).block();

        StepVerifier.create(redisQueue.receive(queueInfo)).expectNext(SignatureData.create(expected, publicKey.getEncoded())).expectComplete().verify();
    }
}
