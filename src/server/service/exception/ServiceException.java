package server.service.exception;

import protocol.response.ErrorType;

public class ServiceException extends RuntimeException {

    //region Properties

    private final ErrorType errorType;

    //endregion

    //region Initialization

    public ServiceException(ErrorType errorType) {
        super(errorType.message);
        this.errorType = errorType;
    }

    public ServiceException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public ServiceException(ErrorType errorType, String message, Throwable cause) {
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
