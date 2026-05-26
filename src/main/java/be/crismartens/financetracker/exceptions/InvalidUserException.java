package be.crismartens.financetracker.exceptions;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException() { super("Invalid e-mail or password"); }

}
