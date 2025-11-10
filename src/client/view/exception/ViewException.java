package client.view.exception;

public class ViewException extends RuntimeException {

    //region Initialization

    public ViewException(String message) {
        super(message);
    }

    public ViewException(String message, Throwable cause) {
        super(message, cause);
    }

    //endregion
}
