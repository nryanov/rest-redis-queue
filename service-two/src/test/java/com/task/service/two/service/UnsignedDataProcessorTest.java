package com.task.service.two.service;

import com.task.common.model.QueueInfo;
import com.task.common.service.ECDSASignature;
import com.task.service.two.queue.Queue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.security.SignatureException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class UnsignedDataProcessorTest {
    @Mock
    private Queue queue;
    @Mock
    private ECDSASignature signature;
    @InjectMocks
    private UnsignedDataProcessor unsignedDataProcessor;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSuccess() throws Exception {
        QueueInfo queueInfo = new QueueInfo("q1", "q2");
        byte[] data = new byte[] {1};
        byte[] sign = new byte[] {2};

        Mockito.when(queue.send(Mockito.any(), Mockito.any())).thenReturn(Mono.just(1L));
        Mockito.when(queue.receive(queueInfo)).thenReturn(Mono.just(data));
        Mockito.when(signature.sign(data)).thenReturn(sign);

        Message message = new DefaultMessage(new byte[0], objectMapper.writeValueAsBytes(queueInfo));
        assertDoesNotThrow(() -> unsignedDataProcessor.onMessage(message, new byte[0]));
    }

    @Test
    public void testFailureLog() throws Exception {
        QueueInfo queueInfo = new QueueInfo("q1", "q2");
        byte[] data = new byte[] {1};

        Mockito.when(queue.send(Mockito.any(), Mockito.any())).thenReturn(Mono.just(1L));
        Mockito.when(queue.receive(queueInfo)).thenReturn(Mono.just(data));
        Mockito.when(signature.sign(data)).thenThrow(new SignatureException("Error"));

        Message message = new DefaultMessage(new byte[0], objectMapper.writeValueAsBytes(queueInfo));
        assertDoesNotThrow(() -> unsignedDataProcessor.onMessage(message, new byte[0]));
    }
}
