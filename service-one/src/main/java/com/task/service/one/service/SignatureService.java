package com.task.service.one.service;

import com.task.common.model.QueueInfo;
import com.task.common.service.ECDSASignature;
import com.task.common.util.DataGenerator;
import com.task.service.one.model.Success;
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

    public Mono<Success> process() {
        return Mono.just(dataGenerator.generate())
                .zipWith(Mono.just(new QueueInfo(dataGenerator.randomQueueName(), dataGenerator.randomQueueName())))
                .doOnNext(data -> logger.info("Unsigned data was generated"))
                .flatMap(
                        unsignedData -> queue
                                .send(unsignedData.getT2(), unsignedData.getT1())
                                .doOnNext(v -> logger.info("Unsigned data was send to the storage"))
                                .map(v -> unsignedData)
                )
                .doOnNext(tuple -> logger.info("Wait result on queue: {}", tuple.getT2().getSignatureQueue()))
                .flatMap(
                        tuple -> queue.receive(tuple.getT2())
                        .doOnNext(data -> logger.info("Got signature"))
                        .zipWith(Mono.just(tuple))
                )
                .doOnNext(data -> logger.info("Verify data signature"))
                .flatMap(triple -> {
                    try {
                        return Mono.just(signature.verify(triple.getT2().getT1(), triple.getT1()))
                                .flatMap(result -> {
                                    if (result) {
                                        return Mono.just(Success.create(triple.getT2().getT1(), triple.getT1()));
                                    } else {
                                        return Mono.error(new RuntimeException("Incorrect signature"));
                                    }
                                });
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }
}
