package protocol.response;

public class Response {
    private final StatusCode statusCode;
    private final String body;

    public Response(StatusCode statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}
