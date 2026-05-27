package be.crismartens.financetracker.exceptions;

public class NullValueException extends RuntimeException {
    public NullValueException() {
        super("Value can't be null");
    }
}
