package com.task.service.one.model;

import java.util.Objects;

public class Failure {
    private String reason;

    public static Failure create(String reason) {
        return new Failure(reason);
    }

    public static Failure create(Throwable reason) {
        return new Failure(reason);
    }

    public Failure() {
    }

    public Failure(Throwable err) {
        this.reason = err.getLocalizedMessage();
    }

    public Failure(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Failure failure = (Failure) o;
        return Objects.equals(reason, failure.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reason);
    }

    @Override
    public String toString() {
        return "Failure{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
