package be.crismartens.financetracker;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException() { super("Invalid e-mail or password"); }

}
