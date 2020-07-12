package com.task.service.two.queue;

import com.task.common.model.QueueInfo;
import com.task.common.model.SignatureData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.test.StepVerifier;

import java.security.PublicKey;
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
    private ReactiveRedisTemplate<String, SignatureData> signedTemplate;
    @Autowired
    @Qualifier("unsigned")
    private ReactiveRedisTemplate<String, byte[]> unsignedTemplate;
    @Autowired
    private PublicKey publicKey;

    @Test
    public void sendTest() {
        byte[] data = new byte[] {1};
        QueueInfo queueInfo = new QueueInfo("q1", "q2");

        redisQueue.send(queueInfo, data).block();

        StepVerifier.create(signedTemplate.opsForList().leftPop(queueInfo.getSignatureQueue()))
                .expectNext(SignatureData.create(data, publicKey.getEncoded()))
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
