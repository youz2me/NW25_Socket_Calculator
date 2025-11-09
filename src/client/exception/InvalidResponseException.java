package client.exception;

public class InvalidResponseException extends NetworkException {

    public InvalidResponseException() {
        super("Invalid response from server");
    }

    public InvalidResponseException(String message) {
        super(message);
    }

    public InvalidResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
