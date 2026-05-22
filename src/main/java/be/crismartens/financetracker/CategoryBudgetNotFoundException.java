package be.crismartens.financetracker;

public class CategoryBudgetNotFoundException extends RuntimeException {

    public CategoryBudgetNotFoundException(String category) {
        super("budget for category " + category + " not found");
    }

    public CategoryBudgetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
