package be.crismartens.financetracker.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetRequestBody {
    private String categoryName;
    private Double amount;

    public BudgetRequestBody(String categoryName, Double amount) {
        this.categoryName = categoryName;
        this.amount = amount;
    }
}
