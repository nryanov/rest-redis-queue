package com.task.common.model;

import java.util.Arrays;

public class SignedData {
    private byte[] data;
    private byte[] signature;
    private boolean isValid;

    public static SignedData create(byte[] data, byte[] signature) {
        return new SignedData(data, signature);
    }

    public SignedData() {
    }

    public SignedData(byte[] data, byte[] signature) {
        this.data = data;
        this.signature = signature;
        this.isValid = false;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignedData that = (SignedData) o;
        return Arrays.equals(data, that.data) &&
                Arrays.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(data);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }

    @Override
    public String toString() {
        return "SignedData{" +
                "isValid=" + isValid +
                '}';
    }
}
