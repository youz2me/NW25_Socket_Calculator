package server.exception;

import protocol.response.ErrorType;

public class InvalidOperationException extends RuntimeException {

    //region Properties

    private final ErrorType errorType;

    //endregion

    //region Initialization

    public InvalidOperationException() {
        super(ErrorType.INVALID_OPERATION.message);
        this.errorType = ErrorType.INVALID_OPERATION;
    }

    public InvalidOperationException(String message) {
        super(message);
        this.errorType = ErrorType.INVALID_OPERATION;
    }

    //endregion

    //region Getters

    public ErrorType getErrorType() {
        return errorType;
    }

    //endregion
}
