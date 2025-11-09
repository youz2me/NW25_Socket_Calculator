package client.exception;

public class NetworkException extends RuntimeException {

    //region Initialization

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    //endregion
}
