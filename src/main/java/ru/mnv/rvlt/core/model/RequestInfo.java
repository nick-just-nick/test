package ru.mnv.rvlt.core.model;

import java.util.Objects;

class RequestInfo {
    private final String comment;
    private final Long requestId;

    public RequestInfo(String comment, Long requestId) {
        Objects.requireNonNull(requestId, "Empty request ID");
        this.comment = comment;
        this.requestId = requestId;
    }

    public String getComment() {
        return comment;
    }

    public Long getRequestId() {
        return requestId;
    }
}
