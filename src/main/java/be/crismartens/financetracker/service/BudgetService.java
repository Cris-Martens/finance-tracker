package be.crismartens.financetracker.service;

import be.crismartens.financetracker.CategoryNotFoundException;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.model.Category;
import be.crismartens.financetracker.model.CategoryBudget;
import be.crismartens.financetracker.model.CategoryBudgetDTO;
import be.crismartens.financetracker.repository.BudgetRepository;
import be.crismartens.financetracker.repository.CategoryRepository;
import be.crismartens.financetracker.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Setter
@Getter
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

    @Transactional
    public void saveBudgets(UserDetails principal, Map<String, Double> budgets) {
        System.out.println("Saving budgets");
        AppUser user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        for (String key : budgets.keySet()) {
            System.out.println("Saving budget: " + key);
            if (budgets.get(key) > 0) {
                System.out.println(key + " - " + budgets.get(key));
                CategoryBudget budget = new CategoryBudget();
                Category category = categoryRepository.findByName(key)
                        .orElseThrow(() -> new CategoryNotFoundException("category not found"));
                budget.setCategory(category);
                budget.setAmount(budgets.get(key));
                budget.setAppUser(user);

                budgetRepository.save(budget);
                System.out.println("Success!");
            }
        }
    }

    public Map<String, Double> getAllBudgets(UserDetails principal) {
        Long userId = userRepository.findIdByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        List<CategoryBudget> budgets = budgetRepository.getCategoryBudgetByAppUser_Id(userId);
        Map<String, Double> budgetsMap = new LinkedHashMap<>();
        for  (CategoryBudget budget : budgets) {
            budgetsMap.put(budget.getCategory().getName(), budget.getAmount());
        }
        return budgetsMap;
    }
}
