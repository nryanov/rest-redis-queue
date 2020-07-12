package com.task.service.one.queue;

import com.task.common.model.QueueInfo;
import com.task.common.model.SignatureData;
import reactor.core.publisher.Mono;

public interface Queue {
    Mono<Long> send(QueueInfo queueInfo, byte[] data);

    Mono<SignatureData> receive(QueueInfo queueInfo);
}
