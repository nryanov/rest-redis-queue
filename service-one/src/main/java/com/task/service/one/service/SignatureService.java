package com.task.service.one.service;

import com.task.common.model.SignedData;
import com.task.common.model.UnsignedData;
import com.task.common.service.ECDSASignature;
import com.task.common.util.DataGenerator;
import com.task.service.one.queue.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class SignatureService {
    private final static Logger logger = LoggerFactory.getLogger(SignatureService.class);

    private final Queue queue;
    private final DataGenerator dataGenerator;
    private final ECDSASignature signature;

    public SignatureService(Queue queue, DataGenerator dataGenerator, ECDSASignature signature) {
        this.queue = queue;
        this.dataGenerator = dataGenerator;
        this.signature = signature;
    }

    public Mono<SignedData> process() {
        return Mono.just(UnsignedData.create(dataGenerator.randomQueueName(), dataGenerator.randomQueueName(), dataGenerator.generate()))
                .doOnNext(data -> logger.info("Unsigned data was generated"))
                .flatMap(
                        unsignedData -> queue
                                .send(unsignedData)
                                .doOnNext(v -> logger.info("Unsigned data was send to the storage"))
                                .map(v -> unsignedData.getToQueue())
                )
                .doOnNext(queue -> logger.info("Wait result on queue: {}", queue))
                .flatMap(
                        targetQueue -> queue.receive(targetQueue)
                        .doOnNext(data -> logger.info("Signed data: {}", data))
                )
                .doOnNext(data -> logger.info("Verify data signature"))
                .flatMap(signedData -> {
                    try {
                        return Mono.just(signature.verify(signedData.getData(), signedData.getSignature()))
                                .doOnNext(signedData::setValid)
                                .map(a -> signedData);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }
}
