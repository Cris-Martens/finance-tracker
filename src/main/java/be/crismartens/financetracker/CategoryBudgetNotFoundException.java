package be.crismartens.financetracker;

public class CategoryBudgetNotFoundException extends RuntimeException {

    public CategoryBudgetNotFoundException(String message) {
        super(message);
    }

    public CategoryBudgetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
