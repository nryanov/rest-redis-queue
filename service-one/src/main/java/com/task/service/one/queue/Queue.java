package com.task.service.one.queue;

import com.task.common.model.QueueInfo;
import reactor.core.publisher.Mono;

public interface Queue {
    Mono<Long> send(QueueInfo queueInfo, byte[] data);

    Mono<byte[]> receive(QueueInfo queueInfo);
}
