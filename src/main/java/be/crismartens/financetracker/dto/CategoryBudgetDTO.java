package be.crismartens.financetracker.dto;

import be.crismartens.financetracker.model.CategoryBudget;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;

public class CategoryBudgetDTO {
    @JsonProperty(value = "category_name")
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
