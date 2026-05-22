package be.crismartens.financetracker;

public class NullValueException extends RuntimeException {
    public NullValueException() {
        super("Value can't be null");
    }
}
