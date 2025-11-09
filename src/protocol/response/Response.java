package protocol.response;

public class Response {
    private final StatusCode statusCode;
    private final String message;
    private final String data;

    public Response(StatusCode statusCode, String message, String data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
}
