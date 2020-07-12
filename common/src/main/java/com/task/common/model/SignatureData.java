package com.task.common.model;

import java.util.Arrays;

public class SignatureData {
    private byte[] signature;
    private byte[] publicKey;

    public static SignatureData create(byte[] signature, byte[] publicKey) {
        return new SignatureData(signature, publicKey);
    }

    public SignatureData() {
    }

    public SignatureData(byte[] signature, byte[] publicKey) {
        this.signature = signature;
        this.publicKey = publicKey;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignatureData signature1 = (SignatureData) o;
        return Arrays.equals(signature, signature1.signature) &&
                Arrays.equals(publicKey, signature1.publicKey);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(signature);
        result = 31 * result + Arrays.hashCode(publicKey);
        return result;
    }
}
