package com.task.service.one.queue;

import com.task.common.model.SignedData;
import com.task.common.model.UnsignedData;
import reactor.core.publisher.Mono;

public interface Queue {
    Mono<Long> send(UnsignedData data);

    Mono<SignedData> receive(String queue);
}
