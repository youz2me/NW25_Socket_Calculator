package server.service.exception;

import protocol.response.ErrorType;

public class DivideByZeroException extends ServiceException {

    public DivideByZeroException() {
        super(ErrorType.DIVIDE_BY_ZERO, "Cannot divide by zero");
    }

    public DivideByZeroException(String message) {
        super(ErrorType.DIVIDE_BY_ZERO, message);
    }
}
