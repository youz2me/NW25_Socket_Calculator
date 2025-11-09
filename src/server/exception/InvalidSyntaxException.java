package server.exception;

import protocol.response.ErrorType;

public class InvalidSyntaxException extends CalculatorException {

    public InvalidSyntaxException() {
        super(ErrorType.INVALID_SYNTAX, "Invalid syntax");
    }

    public InvalidSyntaxException(String message) {
        super(ErrorType.INVALID_SYNTAX, message);
    }
}
