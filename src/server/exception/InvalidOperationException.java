package server.exception;

import protocol.response.ErrorType;

public class InvalidOperationException extends CalculatorException {

    public InvalidOperationException() {
        super(ErrorType.INVALID_OPERATION, "Invalid operation");
    }

    public InvalidOperationException(String message) {
        super(ErrorType.INVALID_OPERATION, message);
    }
}
