package be.crismartens.financetracker;

public class NoIncomeAddedException extends RuntimeException {
    public NoIncomeAddedException(long userId) {
        super("User with id " + userId + " has no income added.");
    }
}
