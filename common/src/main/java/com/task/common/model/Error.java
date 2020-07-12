package com.task.common.model;

import java.util.Objects;

public class Error {
    private String reason;

    public Error() {
    }

    public Error(String reason) {
        this.reason = reason;
    }

    public Error(Throwable reason) {
        this.reason = reason.getLocalizedMessage();
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
        Error error = (Error) o;
        return Objects.equals(reason, error.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reason);
    }

    @Override
    public String toString() {
        return "Error{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
