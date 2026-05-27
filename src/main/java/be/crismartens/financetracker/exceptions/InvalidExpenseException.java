package be.crismartens.financetracker.exceptions;

public class InvalidExpenseException extends RuntimeException {
    public InvalidExpenseException(String message) {
        super("Expense contains invalid arguments");
    }
}
