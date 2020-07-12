package com.task.service.two.queue;

import com.task.common.model.SignedData;
import com.task.common.model.UnsignedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RedisQueue implements Queue {
    private static final Logger logger = LoggerFactory.getLogger(RedisQueue.class);
    private final ReactiveRedisTemplate<String, SignedData> signedTemplate;
    private final ReactiveRedisTemplate<String, UnsignedData> unsignedTemplate;

    public RedisQueue(@Qualifier("signed") ReactiveRedisTemplate<String, SignedData> signedTemplate, @Qualifier("unsigned") ReactiveRedisTemplate<String, UnsignedData> unsignedTemplate) {
        this.signedTemplate = signedTemplate;
        this.unsignedTemplate = unsignedTemplate;
    }

    @Override
    public Mono<UnsignedData> receive(String from) {
        logger.info("Receive unsigned data from queue");
        return unsignedTemplate
                .opsForList()
                .leftPop(from)
                .switchIfEmpty(Mono.error(new RuntimeException("Task queue was empty")));
    }

    @Override
    public Mono<Long> send(String to, SignedData data) {
        logger.info("Send signed data to the destination queue");
        return signedTemplate.opsForList().leftPush(to, data);
    }
}
