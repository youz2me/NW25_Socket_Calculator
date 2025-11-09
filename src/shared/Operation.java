package shared;

public enum Operation {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/");

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    public static Operation fromSymbol(String symbol) {
        return switch (symbol) {
            case "+" -> ADD;
            case "-" -> SUB;
            case "*" -> MUL;
            case "/" -> DIV;
            default -> throw new IllegalArgumentException("Unknown operation: " + symbol);
        };
    }

    public static Operation fromString(String name) {
        return switch (name.toUpperCase()) {
            case "ADD" -> ADD;
            case "SUB" -> SUB;
            case "MUL" -> MUL;
            case "DIV" -> DIV;
            default -> throw new IllegalArgumentException("Invalid operation: " + name);
        };
    }
}
