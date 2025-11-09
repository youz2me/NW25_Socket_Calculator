package server.exception;

import protocol.response.ErrorType;

public class CalculatorException extends RuntimeException {

    //region Properties

    private final ErrorType errorType;

    //endregion

    //region Initialization

    public CalculatorException(ErrorType errorType) {
        super(errorType.message);
        this.errorType = errorType;
    }

    public CalculatorException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public CalculatorException(ErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    //endregion

    //region Getters

    public ErrorType getErrorType() {
        return errorType;
    }

    //endregion
}
