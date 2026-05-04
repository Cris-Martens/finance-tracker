package be.crismartens.financetracker;

public class EmptyExpenseException extends RuntimeException {
    public EmptyExpenseException(String message) {
        super(message);
    }
}
