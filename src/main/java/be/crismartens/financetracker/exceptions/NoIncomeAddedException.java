package be.crismartens.financetracker.exceptions;

public class NoIncomeAddedException extends RuntimeException {
    public NoIncomeAddedException(long userId) {
        super("User with id " + userId + " has no income added.");
    }
}
