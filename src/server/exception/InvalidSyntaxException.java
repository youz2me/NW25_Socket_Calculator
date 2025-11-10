package server.exception;

import protocol.response.ErrorType;

public class InvalidSyntaxException extends RuntimeException {

    //region Properties

    private final ErrorType errorType;

    //endregion

    //region Initialization

    public InvalidSyntaxException() {
        super(ErrorType.INVALID_SYNTAX.message);
        this.errorType = ErrorType.INVALID_SYNTAX;
    }

    public InvalidSyntaxException(String message) {
        super(message);
        this.errorType = ErrorType.INVALID_SYNTAX;
    }

    //endregion

    //region Getters

    public ErrorType getErrorType() {
        return errorType;
    }

    //endregion
}
