package be.crismartens.financetracker.dto;

import be.crismartens.financetracker.model.CategoryBudget;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;

public class CategoryBudgetDTO {
    @JsonProperty(value = "category_name")
    @Schema(
            description = "Name of the category for which the budget is set",
            example = "Housing"
    )
    private String categoryName;
    @Schema(
            description = "Expected monthly expenses for this category",
            example = "750.00"
    )
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
