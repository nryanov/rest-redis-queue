package com.task.service.two.queue;

import com.task.common.model.QueueInfo;
import com.task.common.model.SignatureData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.PublicKey;

@Service
public class RedisQueue implements Queue {
    private static final Logger logger = LoggerFactory.getLogger(RedisQueue.class);
    private final ReactiveRedisTemplate<String, SignatureData> signedTemplate;
    private final ReactiveRedisTemplate<String, byte[]> unsignedTemplate;
    private final PublicKey publicKey;

    public RedisQueue(@Qualifier("signed") ReactiveRedisTemplate<String, SignatureData> signedTemplate,
                      @Qualifier("unsigned") ReactiveRedisTemplate<String, byte[]> unsignedTemplate,
                      PublicKey publicKey) {
        this.signedTemplate = signedTemplate;
        this.unsignedTemplate = unsignedTemplate;
        this.publicKey = publicKey;
    }

    @Override
    public Mono<byte[]> receive(QueueInfo queueInfo) {
        logger.info("Receive unsigned data from queue");
        return unsignedTemplate
                .opsForList()
                .leftPop(queueInfo.getUnsignedDataQueue())
                .switchIfEmpty(Mono.error(new RuntimeException("Task queue was empty")));
    }

    @Override
    public Mono<Long> send(QueueInfo queueInfo, byte[] data) {
        logger.info("Send signed data to the destination queue");
        return signedTemplate.opsForList().leftPush(queueInfo.getSignatureQueue(), SignatureData.create(data, publicKey.getEncoded()));
    }
}
