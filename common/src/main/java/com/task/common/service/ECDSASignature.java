package com.task.common.service;

import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import com.task.common.model.SignatureData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ECDSASignature {
    private final static Logger logger = LoggerFactory.getLogger(ECDSASignature.class);

    @Value("${signature.algorithm}")
    private String algorithm;

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public ECDSASignature(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public byte[] sign(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        logger.info("Signing data");
        logger.debug("Data: {}", data);
        Signature ecdsaSign = Signature.getInstance(algorithm);
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(data);

        return ecdsaSign.sign();
    }

    public boolean verify(byte[] data, byte[] signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        logger.info("Verify signature");
        logger.debug("Data: {} \nSignature: {}", data, signature);
        Signature ecdsaVerify = Signature.getInstance(algorithm);
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(data);

        return ecdsaVerify.verify(signature);
    }

    public boolean verify(byte[] data, SignatureData signatureData) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidKeySpecException {
        logger.info("Verify signature");
        logger.debug("Data: {} \nSignature: {}", data, signatureData.getSignature());
        Signature ecdsaVerify = Signature.getInstance(algorithm);

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(signatureData.getPublicKey());
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(data);

        return ecdsaVerify.verify(signatureData.getSignature());
    }
}
