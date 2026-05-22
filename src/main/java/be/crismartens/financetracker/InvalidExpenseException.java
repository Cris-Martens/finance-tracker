package be.crismartens.financetracker;

public class InvalidExpenseException extends RuntimeException {
    public InvalidExpenseException(String message) {
        super("Expense contains invalid arguments");
    }
}
