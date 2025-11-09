package server.exception;

import protocol.response.ErrorType;

public class DivideByZeroException extends CalculatorException {

    public DivideByZeroException() {
        super(ErrorType.DIVIDE_BY_ZERO, "Cannot divide by zero");
    }

    public DivideByZeroException(String message) {
        super(ErrorType.DIVIDE_BY_ZERO, message);
    }
}
