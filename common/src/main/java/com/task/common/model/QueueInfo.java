package com.task.common.model;

import java.util.Objects;

/**
 * unsignedDataQueue - storage for unsigned data
 * signatureQueue - storage for signature
 */
public class QueueInfo {
    private String unsignedDataQueue;
    private String signatureQueue;

    public QueueInfo() {
    }

    public QueueInfo(String unsignedDataQueue, String signatureQueue) {
        this.unsignedDataQueue = unsignedDataQueue;
        this.signatureQueue = signatureQueue;
    }

    public String getUnsignedDataQueue() {
        return unsignedDataQueue;
    }

    public void setUnsignedDataQueue(String unsignedDataQueue) {
        this.unsignedDataQueue = unsignedDataQueue;
    }

    public String getSignatureQueue() {
        return signatureQueue;
    }

    public void setSignatureQueue(String signatureQueue) {
        this.signatureQueue = signatureQueue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueueInfo queueInfo = (QueueInfo) o;
        return Objects.equals(unsignedDataQueue, queueInfo.unsignedDataQueue) &&
                Objects.equals(signatureQueue, queueInfo.signatureQueue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unsignedDataQueue, signatureQueue);
    }

    @Override
    public String toString() {
        return "QueueInfo{" +
                "from='" + unsignedDataQueue + '\'' +
                ", to='" + signatureQueue + '\'' +
                '}';
    }
}
