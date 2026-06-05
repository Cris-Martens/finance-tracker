package be.crismartens.financetracker.dto;

import be.crismartens.financetracker.model.Category;
import be.crismartens.financetracker.model.Expense;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Objects;

public class ExpenseDTO {
    @Schema(
            description = "Unique identifier long value",
            example = "12345"
    )
    private Long id;
    @JsonProperty("category")
    @Schema(
            description = "Category to which this expense falls under",
            example = """
                    {
                        "id": "1",
                        "name": "Housing"
                    }
                    """
    )
    private Category category;

    @JsonProperty("expense_date")
    @Schema(
            description = "Date when the expense was made",
            example = "2026-05-11"
    )
    LocalDate expenseDate;
    @Schema(
            description = "Amount of the expense",
            example = "750.00"
    )
    private double amount;
    @Schema(
            description = "Optional additional information about the expense",
            example = "Rent"
    )
    private String description;

    public ExpenseDTO() {}

    public ExpenseDTO(Expense expense) {
        this.id = expense.getId();
        this.category = expense.getCategory();
        this.expenseDate = expense.getExpenseDate();
        this.amount = expense.getAmount();
        this.description = expense.getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseDTO that = (ExpenseDTO) o;
        return Double.compare(amount, that.amount) == 0 && Objects.equals(id, that.id) && Objects.equals(category, that.category) && Objects.equals(expenseDate, that.expenseDate) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, expenseDate, amount, description);
    }
}


