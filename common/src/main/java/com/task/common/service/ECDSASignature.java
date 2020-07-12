package com.task.common.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

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
        // todo: debug
        logger.info("Data: {}", data);
        Signature ecdsaSign = Signature.getInstance(algorithm);
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(data);

        return ecdsaSign.sign();
    }

    public boolean verify(byte[] data, byte[] signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        logger.info("Verify signature");
        // todo: debug
        logger.info("Data: {} \nSignature: {}", data, signature);
        Signature ecdsaVerify = Signature.getInstance(algorithm);
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(data);

        return ecdsaVerify.verify(signature);
    }
}
