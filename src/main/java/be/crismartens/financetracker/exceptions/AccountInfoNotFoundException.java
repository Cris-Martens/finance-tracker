package be.crismartens.financetracker.exceptions;

public class AccountInfoNotFoundException extends RuntimeException {
    public AccountInfoNotFoundException(Long id) {
        super("Account info for user with id: " + id + " not found");
    }
}
