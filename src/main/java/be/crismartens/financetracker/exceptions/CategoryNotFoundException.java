package be.crismartens.financetracker.exceptions;

public class CategoryNotFoundException extends RuntimeException {
  public CategoryNotFoundException(String category) {
    super("category with name " + category + " not found");
  }
}
