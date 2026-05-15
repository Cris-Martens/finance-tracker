package be.crismartens.financetracker;

public class UsernameAlreadyInUseExcepion extends RuntimeException{
    public UsernameAlreadyInUseExcepion(String message) {
        super(message);
    }
}
