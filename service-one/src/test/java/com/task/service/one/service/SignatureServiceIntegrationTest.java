package com.task.service.one.service;

import com.task.common.model.SignatureData;
import com.task.common.service.ECDSASignature;
import com.task.common.util.DataGenerator;
import com.task.service.one.AbstractRedisTest;
import com.task.service.one.model.Success;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.test.StepVerifier;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

@SpringBootTest(properties = {
        "queue.destination:topic",
        "queue.timeout:10",
        "signature.parameters:prime256v1",
        "signature.algorithm:SHA256withECDSA",
})
public class SignatureServiceIntegrationTest extends AbstractRedisTest {
    @Autowired
    @Qualifier("signed")
    private ReactiveRedisTemplate<String, SignatureData> signedTemplate;
    @Autowired
    private SignatureService signatureService;
    @Autowired
    private ECDSASignature signature;
    @MockBean
    private DataGenerator generator;

    @Test
    public void test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        byte[] data = new byte[] {1};
        byte[] sign = signature.sign(data);
        SignatureData signatureData = SignatureData.create(sign, new byte[] {3});

        Mockito.when(generator.generate()).thenReturn(data);
        Mockito.when(generator.randomQueueName()).thenReturn("q1", "q2");
        signedTemplate.opsForList().leftPush("q2", signatureData).block();

        StepVerifier.create(signatureService.process()).expectNext(Success.create(data, sign)).expectComplete().verify();
    }
}
