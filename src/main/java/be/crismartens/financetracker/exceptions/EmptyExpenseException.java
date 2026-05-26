package be.crismartens.financetracker.exceptions;

public class EmptyExpenseException extends RuntimeException {
    public EmptyExpenseException(String message) {
        super(message);
    }
}
