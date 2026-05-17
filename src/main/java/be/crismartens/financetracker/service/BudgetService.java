package be.crismartens.financetracker.service;

import be.crismartens.financetracker.CategoryBudgetNotFoundException;
import be.crismartens.financetracker.CategoryNotFoundException;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.model.Category;
import be.crismartens.financetracker.model.CategoryBudget;
import be.crismartens.financetracker.repository.BudgetRepository;
import be.crismartens.financetracker.repository.CategoryRepository;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         UserRepository userRepository,
                         CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    private Long findAppUserId(UserDetails principal) {
        return userRepository.findIdByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Transactional
    public void saveBudgets(UserDetails principal, Map<String, Double> budgets) {
        // Look up user
        AppUser user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        // Iterate over request budgets
        for (String key : budgets.keySet()) {

            // Check budget is valid
            if (budgets.get(key) < 0) {
                throw new IllegalArgumentException("Budgets cannot be negative");
            }

            CategoryBudget budget = new CategoryBudget();

            // Verify request category exists
            Category category = categoryRepository.findByName(key)
                    .orElseThrow(() -> new CategoryNotFoundException("category not found"));

            budget.setCategory(category);
            budget.setAmount(budgets.get(key));
            budget.setAppUser(user);

            budgetRepository.save(budget);
        }
    }

    public Map<String, Double> getAllBudgets(UserDetails principal) {

        Long userId = findAppUserId(principal);

        List<CategoryBudget> budgets = budgetRepository.getCategoryBudgetByAppUser_Id(userId);

        Map<String, Double> budgetsMap = new LinkedHashMap<>();
        for  (CategoryBudget budget : budgets) {
            budgetsMap.put(budget.getCategory().getName(), budget.getAmount());
        }

        return budgetsMap;
    }

    public void updateBudgets(UserDetails principal, Map<String, Double> budgets) {
        Long userId = findAppUserId(principal);

        if (budgets.isEmpty()) {
            throw new IllegalArgumentException("Budgets cannot be empty");
        }

        for (String key : budgets.keySet()) {
            if (budgets.get(key) == null || budgets.get(key) < 0) {
                throw new IllegalArgumentException("Budgets cannot be negative");
            }

            Long categoryId = categoryRepository.findIdByName(key);
            if (categoryId == null) {
                throw new CategoryNotFoundException("category not found");
            }

            CategoryBudget budget = budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(userId, categoryId);
            if (budget == null) {
                throw new CategoryBudgetNotFoundException("budget not found");
            }

            budget.setAmount(budgets.get(key));

            budgetRepository.save(budget);
        }
    }

    public void deleteBudgets(UserDetails principal, String category) {
        Long userId = findAppUserId(principal);

        if (category == null) {
            throw new IllegalArgumentException("category cannot be null");
        }

        Long categoryId = categoryRepository.findIdByName(category);
        if (categoryId == null) {
            throw new CategoryNotFoundException("category not found");
        }
        CategoryBudget budget = budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(userId, categoryId);
        if (budget == null) {
            throw new CategoryBudgetNotFoundException("budget not found");
        }
        budgetRepository.delete(budget);
    }
}
