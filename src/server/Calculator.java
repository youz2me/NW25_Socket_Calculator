package server;

import shared.Operation;

public class Calculator {

    //region Calculation

    public double calculate(String body) {
        String[] parts = body.split(" ");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid syntax: expected 3 parts");
        }

        String operationStr = parts[0];
        double num1 = Double.parseDouble(parts[1]);
        double num2 = Double.parseDouble(parts[2]);

        Operation operation = Operation.fromString(operationStr);

        return switch (operation) {
            case ADD -> num1 + num2;
            case SUB -> num1 - num2;
            case MUL -> num1 * num2;
            case DIV -> {
                if (num2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                yield num1 / num2;
            }
        };
    }

    //endregion
}
