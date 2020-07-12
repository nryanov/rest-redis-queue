package com.task.service.one.service;

import com.task.common.service.ECDSASignature;
import com.task.common.util.DataGenerator;
import com.task.service.one.model.Success;
import com.task.service.one.queue.Queue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@ExtendWith(SpringExtension.class)
public class SignatureServiceTest {
    @Mock
    private Queue queue;
    @Mock
    private DataGenerator generator;
    @Mock
    private ECDSASignature signature;
    @InjectMocks
    private SignatureService signatureService;

    @Test
    public void testSuccessFlow() throws Exception {
        byte[] data = new byte[] {1, 2, 3};
        byte[] sign = new byte[] {3, 2, 1};

        Mockito.when(generator.generate()).thenReturn(data);
        Mockito.when(generator.randomQueueName()).thenReturn("queue1", "queue2");

        Mockito.when(queue.send(Mockito.any(), Mockito.any())).thenReturn(Mono.just(1L));
        Mockito.when(queue.receive(Mockito.any())).thenReturn(Mono.just(sign));

        Mockito.when(signature.verify(Mockito.any(), Mockito.any())).thenReturn(true);

        StepVerifier.create(signatureService.process()).expectNext(Success.create(data, sign)).expectComplete().verify();
    }

    @Test
    public void testFailureFlowOnSendingUnsignedData() throws Exception {
        byte[] data = new byte[] {1, 2, 3};
        byte[] sign = new byte[] {3, 2, 1};

        Mockito.when(generator.generate()).thenReturn(data);
        Mockito.when(generator.randomQueueName()).thenReturn("queue1", "queue2");

        Mockito.when(queue.send(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException("Error"));
        Mockito.when(queue.receive(Mockito.any())).thenReturn(Mono.just(sign));

        Mockito.when(signature.verify(Mockito.any(), Mockito.any())).thenReturn(true);

        StepVerifier.create(signatureService.process()).expectError(RuntimeException.class).verify();
    }

    @Test
    public void testFailureFlowOnReceivingSignature() throws Exception {
        byte[] data = new byte[] {1, 2, 3};

        Mockito.when(generator.generate()).thenReturn(data);
        Mockito.when(generator.randomQueueName()).thenReturn("queue1", "queue2");

        Mockito.when(queue.send(Mockito.any(), Mockito.any())).thenReturn(Mono.just(1L));
        Mockito.when(queue.receive(Mockito.any())).thenThrow(new RuntimeException("Error"));

        Mockito.when(signature.verify(Mockito.any(), Mockito.any())).thenReturn(true);

        StepVerifier.create(signatureService.process()).expectError(RuntimeException.class).verify();
    }

    @Test
    public void testFailureFlowOnVerifyingSignature() throws Exception {
        byte[] data = new byte[] {1, 2, 3};
        byte[] sign = new byte[] {3, 2, 1};

        Mockito.when(generator.generate()).thenReturn(data);
        Mockito.when(generator.randomQueueName()).thenReturn("queue1", "queue2");

        Mockito.when(queue.send(Mockito.any(), Mockito.any())).thenReturn(Mono.just(1L));
        Mockito.when(queue.receive(Mockito.any())).thenReturn(Mono.just(sign));

        Mockito.when(signature.verify(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException("Error"));

        StepVerifier.create(signatureService.process()).expectError(RuntimeException.class).verify();
    }
}
