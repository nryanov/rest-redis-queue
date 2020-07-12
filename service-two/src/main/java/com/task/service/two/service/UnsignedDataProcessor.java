package com.task.service.two.service;

import com.task.common.model.SignedData;
import com.task.common.service.ECDSASignature;
import com.task.service.two.queue.Queue;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UnsignedDataProcessor implements MessageListener {
    private final Queue queue;
    private final ECDSASignature signature;

    private RedisSerializer<String> redisSerializer = new StringRedisSerializer();

    public UnsignedDataProcessor(Queue queue, ECDSASignature signature) {
        this.queue = queue;
        this.signature = signature;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        process(redisSerializer.deserialize(message.getBody())).block();
    }

    private Mono<Long> process(String sourceQueue) {
        return queue.receive(sourceQueue)
                .flatMap(unsignedData -> {
                    try {
                        return Mono.just(signature.sign(unsignedData.getData())).map(sign -> SignedData.create(unsignedData.getData(), sign)).zipWith(Mono.just(unsignedData.getToQueue()));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                }).flatMap(tuple -> queue.send(tuple.getT2(), tuple.getT1()));
    }
}
