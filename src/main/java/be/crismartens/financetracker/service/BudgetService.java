package be.crismartens.financetracker.service;

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
        AppUser user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        for (String key : budgets.keySet()) {
            if (budgets.get(key) > 0) {
                CategoryBudget budget = new CategoryBudget();
                Category category = categoryRepository.findByName(key)
                        .orElseThrow(() -> new CategoryNotFoundException("category not found"));
                budget.setCategory(category);
                budget.setAmount(budgets.get(key));
                budget.setAppUser(user);

                budgetRepository.save(budget);
            }
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

        for (String key : budgets.keySet()) {
            Long categoryId = categoryRepository.findIdByName(key);
            CategoryBudget budget = budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(userId, categoryId);
            budget.setAmount(budgets.get(key));
            budgetRepository.save(budget);
        }
    }

    public void deleteBudgets(UserDetails principal, String category) {
        Long userId = findAppUserId(principal);

        Long categoryId = categoryRepository.findIdByName(category);

        if (categoryId != null) {
            CategoryBudget budget = budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(userId, categoryId);
            if (budget != null) {
                budgetRepository.delete(budget);
            }
        }
    }
}
