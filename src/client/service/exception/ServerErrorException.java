package client.service.exception;

import protocol.response.StatusCode;

public class ServerErrorException extends NetworkException {

    //region Properties

    private final StatusCode statusCode;

    //endregion

    //region Initialization

    public ServerErrorException(StatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    //endregion

    //region Getters

    public StatusCode getStatusCode() {
        return statusCode;
    }

    //endregion
}
