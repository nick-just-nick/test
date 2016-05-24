package ru.mnv.rvlt.core.model;

final class CompoundRequest implements Request {

    private final Request mainRequest;
    private final Request tailedRequest;

    public CompoundRequest(Request mainRequest, Request tailedRequest) {
        if (mainRequest.equals(tailedRequest))
            throw new IllegalArgumentException("Illegal request configuration: tailed request shouldn't be equal to main");
        this.mainRequest = mainRequest;
        this.tailedRequest = tailedRequest;
    }

    @Override
    public Result process() {
        Result result = mainRequest.process();
        return result.isSuccess() ? tailedRequest.process() : result;
    }

    @Override
    public Long getId() {
        return mainRequest.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompoundRequest that = (CompoundRequest) o;

        if (!mainRequest.equals(that.mainRequest)) return false;
        if (!tailedRequest.equals(that.tailedRequest)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return mainRequest.hashCode();
    }
}
