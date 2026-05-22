package be.crismartens.financetracker;

public class CategoryNotFoundException extends RuntimeException {
  public CategoryNotFoundException(String category) {
    super("category with name " + category + " not found");
  }
}
