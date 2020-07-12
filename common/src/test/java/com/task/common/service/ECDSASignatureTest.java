package com.task.common.service;

import com.task.common.configuration.SignatureConfiguration;
import com.task.common.model.SignatureData;
import com.task.common.util.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "signature.parameters:prime256v1",
        "signature.algorithm:SHA256withECDSA",
})
@ContextConfiguration(classes = SignatureConfiguration.class)
public class ECDSASignatureTest {
    @Autowired
    private ECDSASignature signature;
    @Autowired
    private PublicKey publicKey;

    @Test
    public void correctSignature() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        byte[] data = new byte[] {1, 2, 3};
        byte[] sign = signature.sign(data);

        assertTrue(signature.verify(data, sign));
    }

    @Test
    public void correctSignatureData() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        byte[] data = new byte[] {1, 2, 3};
        byte[] sign = signature.sign(data);
        SignatureData signatureData = SignatureData.create(sign, publicKey.getEncoded());

        assertTrue(signature.verify(data, signatureData));
    }

    @Test
    public void incorrectSignature() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        byte[] data = new byte[] {1, 2, 3};
        byte[] sign = signature.sign(data);
        byte[] newData = new byte[] {1, 2, 3, 4, 5};

        assertFalse(signature.verify(newData, sign));
    }
}
