package be.crismartens.financetracker.exceptions;

public class UsernameAlreadyInUseExcepion extends RuntimeException{
    public UsernameAlreadyInUseExcepion(String message) {
        super(message);
    }
}
