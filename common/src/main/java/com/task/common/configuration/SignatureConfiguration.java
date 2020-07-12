package com.task.common.configuration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

@Configuration
@ComponentScan(basePackages = "com.task.common")
public class SignatureConfiguration {
    @Value("${signature.parameters}")
    private String parameters;

    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    @Bean
    public KeyPairGenerator keyPairGenerator() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(parameters);
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(ecSpec, new SecureRandom());

        return generator;
    }

    @Bean
    public KeyPair keyPair(KeyPairGenerator generator) {
        return generator.generateKeyPair();
    }

    @Bean
    public PublicKey publicKey(KeyPair keyPair) {
        return keyPair.getPublic();
    }

    @Bean
    public PrivateKey privateKey(KeyPair keyPair) {
        return keyPair.getPrivate();
    }
}
