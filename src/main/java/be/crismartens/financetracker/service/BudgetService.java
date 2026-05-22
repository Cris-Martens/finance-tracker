package be.crismartens.financetracker.service;

import be.crismartens.financetracker.CategoryBudgetNotFoundException;
import be.crismartens.financetracker.CategoryNotFoundException;
import be.crismartens.financetracker.dto.CategoryBudgetDTO;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.model.BudgetRequestBody;
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
    public CategoryBudgetDTO saveBudgets(UserDetails principal, BudgetRequestBody budgets) {
        // Look up user
        AppUser user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        CategoryBudget budget = new CategoryBudget();


        // Check budget is valid
        if (budgets.getAmount() < 0) {
            throw new IllegalArgumentException("Budgets cannot be negative");
        }

        // Verify request category exists
        Category category = categoryRepository.findByName(budgets.getCategoryName())
                .orElseThrow(() -> new CategoryNotFoundException("category not found"));

        budget.setCategory(category);
        budget.setAmount(budgets.getAmount());
        budget.setAppUser(user);

        budgetRepository.save(budget);

        return new CategoryBudgetDTO(budget);
    }

    public List<CategoryBudgetDTO> getAllBudgets(UserDetails principal) {

        Long userId = findAppUserId(principal);

        List<CategoryBudget> budgets = budgetRepository.getCategoryBudgetByAppUser_Id(userId);

        List<CategoryBudgetDTO> budgetDTOs = new ArrayList<>();

        for (CategoryBudget budget : budgets) {
            budgetDTOs.add(new CategoryBudgetDTO(budget));
        }

        return budgetDTOs;
    }

    public CategoryBudgetDTO updateBudgets(UserDetails principal, BudgetRequestBody budgets) {
        Long userId = findAppUserId(principal);

        if (budgets == null) {
            throw new IllegalArgumentException("Budgets cannot be empty");
        }

        if (budgets.getAmount() == null || budgets.getAmount() < 0) {
            throw new IllegalArgumentException("Budgets cannot be negative");
        }

        Long categoryId = categoryRepository.findIdByName(budgets.getCategoryName());
        if (categoryId == null) {
            throw new CategoryNotFoundException("category not found");
        }

        CategoryBudget budget = budgetRepository.getCategoryBudgetByAppUser_IdAndCategory_Id(userId, categoryId);
        if (budget == null) {
            throw new CategoryBudgetNotFoundException("budget not found");
        }

        budget.setAmount(budgets.getAmount());

        budgetRepository.save(budget);

        return new CategoryBudgetDTO(budget);
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
