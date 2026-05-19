package be.crismartens.financetracker;

public class AccountInfoNotFoundException extends RuntimeException {
  public AccountInfoNotFoundException(String message) {
    super(message);
  }
}
