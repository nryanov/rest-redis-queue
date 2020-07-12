package com.task.service.one.model;

import java.util.Arrays;

public class Success {
    private byte[] data;
    private byte[] signature;

    public static Success create(byte[] data, byte[] signature) {
        return new Success(data, signature);
    }

    public Success() {
    }

    public Success(byte[] data, byte[] signature) {
        this.data = data;
        this.signature = signature;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Success success = (Success) o;
        return Arrays.equals(data, success.data) &&
                Arrays.equals(signature, success.signature);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(data);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }

    @Override
    public String toString() {
        return "Success";
    }
}
