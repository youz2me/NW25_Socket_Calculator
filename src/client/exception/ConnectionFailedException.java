package client.exception;

public class ConnectionFailedException extends NetworkException {

    public ConnectionFailedException() {
        super("Failed to connect to server");
    }

    public ConnectionFailedException(String message) {
        super(message);
    }

    public ConnectionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
