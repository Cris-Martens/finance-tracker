package be.crismartens.financetracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class ExpenseDTO {
    private Long id;
    @JsonProperty("category_id")
    private int categoryId;
    @JsonProperty("expense_date")
    LocalDate expenseDate;
    private double amount;
    private String description;

    public ExpenseDTO() {}

    public ExpenseDTO(Expense expense) {
        this.id = expense.getId();
        this.categoryId = expense.getCategoryId();
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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
}


