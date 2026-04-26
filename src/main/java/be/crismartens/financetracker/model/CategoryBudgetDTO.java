package be.crismartens.financetracker.model;

public class CategoryBudgetDTO {
    private String categoryName;
    private double amount;

    public CategoryBudgetDTO(CategoryBudget categoryBudget) {
        this.categoryName = categoryBudget.getCategory().getName();
        this.amount = categoryBudget.getAmount();
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryName(CategoryBudget categoryBudget) {
        this.categoryName = categoryBudget.getCategory().getName();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setAmount(CategoryBudget categoryBudget) {
        this.amount = categoryBudget.getAmount();
    }
}
