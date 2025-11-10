package server.service;

import server.service.exception.DivideByZeroException;
import server.exception.InvalidSyntaxException;
import server.exception.InvalidOperationException;
import core.Operation;

public final class CalculatorService {

    //region Calculation

    public double calculate(String body) {
        String[] parts = body.split(" ");

        if (parts.length != 3) {
            throw new InvalidSyntaxException();
        }

        String operationStr = parts[0];
        double num1;
        double num2;

        try {
            num1 = Double.parseDouble(parts[1]);
            num2 = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            throw new InvalidSyntaxException();
        }

        Operation operation;
        try {
            operation = Operation.fromString(operationStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException();
        }

        return switch (operation) {
            case ADD -> num1 + num2;
            case SUB -> num1 - num2;
            case MUL -> num1 * num2;
            case DIV -> {
                if (num2 == 0) {
                    throw new DivideByZeroException();
                }
                yield num1 / num2;
            }
        };
    }

    //endregion
}
