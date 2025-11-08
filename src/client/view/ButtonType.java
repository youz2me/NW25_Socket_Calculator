package client.view;

public enum ButtonType {
    NUMBER,      // 0-9
    DECIMAL,     // .
    OPERATOR,    // +, -, ×, ÷
    EQUALS,      // =
    CLEAR;       // AC

    public static ButtonType from(String command) {
        return switch (command) {
            case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> NUMBER;
            case "." -> DECIMAL;
            case "+", "-", "×", "÷" -> OPERATOR;
            case "=" -> EQUALS;
            case "AC" -> CLEAR;
            default -> throw new IllegalArgumentException("Unknown button: " + command);
        };
    }
}
