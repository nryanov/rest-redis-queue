package com.task.service.two.queue;

import com.task.common.model.QueueInfo;
import reactor.core.publisher.Mono;

public interface Queue {
    Mono<byte[]> receive(QueueInfo queueInfo);

    Mono<Long> send(QueueInfo queueInfo, byte[] data);
}
