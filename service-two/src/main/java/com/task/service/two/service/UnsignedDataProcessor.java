package com.task.service.two.service;

import com.task.common.model.QueueInfo;
import com.task.common.service.ECDSASignature;
import com.task.service.two.queue.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UnsignedDataProcessor implements MessageListener {
    private final static Logger logger = LoggerFactory.getLogger(UnsignedDataProcessor.class);
    private final Queue queue;
    private final ECDSASignature signature;

    private RedisSerializer<QueueInfo> redisSerializer = new Jackson2JsonRedisSerializer<>(QueueInfo.class);

    public UnsignedDataProcessor(Queue queue, ECDSASignature signature) {
        this.queue = queue;
        this.signature = signature;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        logger.info("Got next task");
        process(redisSerializer.deserialize(message.getBody()))
                .doOnError(err -> logger.error(err.getLocalizedMessage()))
                .onErrorReturn(0L)
                .block();
    }

    private Mono<Long> process(QueueInfo queueInfo) {
        return queue.receive(queueInfo)
                .flatMap(unsignedData -> {
                    try {
                        return Mono.just(signature.sign(unsignedData));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                }).flatMap(signature -> queue.send(queueInfo, signature));
    }
}
