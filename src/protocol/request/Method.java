package protocol.request;

public enum Method {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH;

    public static Method fromString(String method) {
        return switch (method.toUpperCase()) {
            case "GET" -> GET;
            case "POST" -> POST;
            case "DELETE" -> DELETE;
            default -> throw new IllegalArgumentException("Unknown HTTP method: " + method);
        };
    }
}
