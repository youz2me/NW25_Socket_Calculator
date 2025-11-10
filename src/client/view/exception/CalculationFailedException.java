package client.view.exception;

public class CalculationFailedException extends ViewException {

    public CalculationFailedException() {
        super("계산 실패");
    }

    public CalculationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
