package ru.mnv.rvlt.core.model;

public class Result {
    private final Long operationId;
    private final ResultStatus status;
    private final String message;

    public Result(Long operationId, ResultStatus status, String message) {
        this.operationId = operationId;
        this.status = status;
        this.message = message;
    }

    public boolean isSuccess() {
        return ResultStatus.OK.equals(this.status);
    }

    public static Result success(Long operationId){
        return new Result(operationId, ResultStatus.OK, null);
    }

    public static Result fail(Long operationId, ResultStatus status, String message){
        return new Result(operationId, status, message);
    }

    public ResultStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "rqst: " + operationId +" " + status + (isSuccess() ? "" : ": "+message);
    }
}
