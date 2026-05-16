package be.crismartens.financetracker.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "budget")
public class CategoryBudget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double amount;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser appUser;

    public CategoryBudget() {}

    public CategoryBudget(double amount, Category category, AppUser appUser) {
        this.amount = amount;
        this.category = category;
        this.appUser = appUser;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public AppUser getAppUser() {
        return appUser;
    }
    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CategoryBudget budget = (CategoryBudget) o;
        return id == budget.id && Double.compare(amount, budget.amount) == 0 && Objects.equals(category, budget.category) && Objects.equals(appUser, budget.appUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, category, appUser);
    }
}
