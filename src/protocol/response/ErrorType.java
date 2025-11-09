package protocol.response;

public enum ErrorType {
    DIVIDE_BY_ZERO("DIVIDE_BY_ZERO"),
    INVALID_OPERATION("INVALID_OPERATION"),
    INVALID_SYNTAX("INVALID_SYNTAX");

    public final String message;

    ErrorType(String message) {
        this.message = message;
    }

    public static ErrorType fromMessage(String message) {
        return switch (message.toUpperCase()) {
            case "DIVIDE_BY_ZERO" -> DIVIDE_BY_ZERO;
            case "INVALID_OPERATION" -> INVALID_OPERATION;
            case "INVALID_SYNTAX" -> INVALID_SYNTAX;
            default -> throw new IllegalArgumentException("Unknown error type: " + message);
        };
    }
}
