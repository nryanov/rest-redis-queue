package com.task.service.two.queue;

import com.task.common.model.QueueInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.test.StepVerifier;

import java.util.Arrays;

@SpringBootTest(properties = {
        "queue.destination:topic",
        "queue.timeout:10"
})
public class RedisQueueTest extends AbstractRedisTest {
    @Autowired
    private Queue redisQueue;
    @Autowired
    @Qualifier("signed")
    private ReactiveRedisTemplate<String, byte[]> signedTemplate;
    @Autowired
    @Qualifier("unsigned")
    private ReactiveRedisTemplate<String, byte[]> unsignedTemplate;

    @Test
    public void sendTest() {
        byte[] data = new byte[] {1};
        QueueInfo queueInfo = new QueueInfo("q1", "q2");

        redisQueue.send(queueInfo, data).block();

        StepVerifier.create(signedTemplate.opsForList().leftPop(queueInfo.getSignatureQueue()))
                .expectNextMatches(sign -> Arrays.equals(sign, data))
                .expectComplete()
                .verify();
    }

    @Test
    public void receiveTest() {
        byte[] data = new byte[] {1};
        QueueInfo queueInfo = new QueueInfo("q1", "q2");

        unsignedTemplate.opsForList().leftPush(queueInfo.getUnsignedDataQueue(), data).block();

        StepVerifier.create(redisQueue.receive(queueInfo))
                .expectNextMatches(saved -> Arrays.equals(saved, data))
                .expectComplete()
                .verify();
    }
}
