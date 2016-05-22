package ru.mnv.rvlt.core.model;

import java.util.Optional;

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
    public AccountNumber getAccountNumber() {
        return mainRequest.getAccountNumber();
    }

    @Override
    public Long getId() {
        return mainRequest.getId();
    }
}
