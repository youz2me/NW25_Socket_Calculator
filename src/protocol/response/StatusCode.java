package protocol.response;

public enum StatusCode {
    OK(200),
    BAD_REQUEST(400),
    INTERNAL_SERVER_ERROR(500);

    public final int code;

    StatusCode(int code) {
        this.code = code;
    }

    public static StatusCode fromCode(int code) {
        return switch (code) {
            case 200 -> OK;
            case 400 -> BAD_REQUEST;
            case 500 -> INTERNAL_SERVER_ERROR;
            default -> throw new IllegalArgumentException("Unknown status code: " + code);
        };
    }
}
