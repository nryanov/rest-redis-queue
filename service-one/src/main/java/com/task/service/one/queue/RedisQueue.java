package com.task.service.one.queue;

import com.task.common.model.QueueInfo;
import com.task.common.model.SignatureData;
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
    private final ReactiveRedisTemplate<String, SignatureData> signedTemplate;
    private final ReactiveRedisTemplate<String, byte[]> unsignedTemplate;
    private final ReactiveRedisTemplate<String, QueueInfo> publisher;

    @Value("${queue.destination}")
    private String destination;
    @Value("${queue.timeout}")
    private int timeout;

    public RedisQueue(@Qualifier("signed") ReactiveRedisTemplate<String, SignatureData> signedTemplate,
                      @Qualifier("unsigned") ReactiveRedisTemplate<String, byte[]> unsignedTemplate,
                      @Qualifier("publisher") ReactiveRedisTemplate<String, QueueInfo> publisher
    ) {
        this.signedTemplate = signedTemplate;
        this.unsignedTemplate = unsignedTemplate;
        this.publisher = publisher;
    }

    @Override
    public Mono<Long> send(QueueInfo queueInfo, byte[] data) {
        return unsignedTemplate.opsForList()
                .leftPush(queueInfo.getUnsignedDataQueue(), data)
                .doOnNext(l -> logger.info("Unsigned data was saved in the queue"))
                .then(publisher.convertAndSend(destination, queueInfo))
                .doOnNext(l -> logger.info("New task info was published"));
    }

    @Override
    public Mono<SignatureData> receive(QueueInfo queueInfo) {
        logger.info("Try to receive result from: {}", queueInfo.getSignatureQueue());

        return signedTemplate
                .opsForList()
                .leftPop(queueInfo.getSignatureQueue(), Duration.ofSeconds(timeout))
                .switchIfEmpty(Mono.error(new Exception("Empty result")));
    }
}
