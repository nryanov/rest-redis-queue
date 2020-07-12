package com.task.service.one.queue;

import com.task.common.model.SignedData;
import com.task.common.model.UnsignedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RedisQueue implements Queue {
    private final static Logger logger = LoggerFactory.getLogger(RedisQueue.class);
    private final ReactiveRedisTemplate<String, SignedData> signedTemplate;
    private final ReactiveRedisTemplate<String, UnsignedData> unsignedTemplate;
    private final ReactiveRedisTemplate<String, String> publisher;

    @Value("${queue.destination}")
    private String destination;
    @Value("${queue.timeout}")
    private int timeout;

    public RedisQueue(@Qualifier("signed") ReactiveRedisTemplate<String, SignedData> signedTemplate,
                      @Qualifier("unsigned") ReactiveRedisTemplate<String, UnsignedData> unsignedTemplate,
                      ReactiveRedisTemplate<String, String> publisher
    ) {
        this.signedTemplate = signedTemplate;
        this.unsignedTemplate = unsignedTemplate;
        this.publisher = publisher;
    }

    @Override
    public Mono<Long> send(UnsignedData data) {
        return unsignedTemplate.opsForList()
                .leftPush(data.getFromQueue(), data)
                .doOnNext(l -> logger.info("Unsigned data was saved in the queue"))
                .then(publisher.convertAndSend(destination, data.getFromQueue()))
                .doOnNext(l -> logger.info("New task info was published"));
    }

    @Override
    public Mono<SignedData> receive(String queue) {
        logger.info("Try to receive result from: {}", queue);

        return signedTemplate
                .opsForList()
                .leftPop(queue, Duration.ofSeconds(timeout))
                .switchIfEmpty(Mono.error(new Exception("Empty result")));
    }
}
