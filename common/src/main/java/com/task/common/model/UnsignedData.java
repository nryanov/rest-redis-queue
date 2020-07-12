package com.task.common.model;

import java.util.Arrays;
import java.util.Objects;

public class UnsignedData {
    private String fromQueue;
    private String toQueue;
    private byte[] data;

    public static UnsignedData create(String fromQueue, String toQueue, byte[] data) {
        return new UnsignedData(fromQueue, toQueue, data);
    }

    public UnsignedData() {
    }

    public UnsignedData(String fromQueue, String toQueue, byte[] data) {
        this.fromQueue = fromQueue;
        this.toQueue = toQueue;
        this.data = data;
    }

    public String getFromQueue() {
        return fromQueue;
    }

    public void setFromQueue(String fromQueue) {
        this.fromQueue = fromQueue;
    }

    public String getToQueue() {
        return toQueue;
    }

    public void setToQueue(String toQueue) {
        this.toQueue = toQueue;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnsignedData that = (UnsignedData) o;
        return Objects.equals(fromQueue, that.fromQueue) &&
                Objects.equals(toQueue, that.toQueue) &&
                Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fromQueue, toQueue);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "UnsignedData{" +
                "fromQueue='" + fromQueue + '\'' +
                ", toQueue='" + toQueue + '\'' +
                '}';
    }
}
