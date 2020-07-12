package com.task.service.two.queue;

import com.task.common.model.SignedData;
import com.task.common.model.UnsignedData;
import reactor.core.publisher.Mono;

public interface Queue {
    Mono<UnsignedData> receive(String from);

    Mono<Long> send(String to, SignedData data);
}
