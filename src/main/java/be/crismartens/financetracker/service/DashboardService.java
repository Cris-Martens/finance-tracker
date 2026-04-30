package be.crismartens.financetracker.service;

import be.crismartens.financetracker.model.BudgetAndSpendDTO;
import be.crismartens.financetracker.model.CategoryBudget;
import be.crismartens.financetracker.model.ExpenseDTO;
import be.crismartens.financetracker.repository.BudgetRepository;
import be.crismartens.financetracker.repository.CategoryRepository;
import be.crismartens.financetracker.repository.ExpensesRepository;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;

@Service
public class DashboardService {
    private final UserRepository userRepository;
    private final ExpensesRepository expensesRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    public DashboardService(UserRepository userRepository,
                            ExpensesRepository expensesRepository,
                            CategoryRepository categoryRepository, BudgetRepository budgetRepository) {
        this.userRepository = userRepository;
        this.expensesRepository = expensesRepository;
        this.categoryRepository = categoryRepository;
        this.budgetRepository = budgetRepository;
    }

    public List<ExpenseDTO> getLatestExpensesByAppUserId(UserDetails principal) {
        Optional<Long> userId = userRepository.findIdByUsername(principal.getUsername());
        if(userId.isPresent()) {
            List<ExpenseDTO> expenses = expensesRepository
                    .findTop5ByAppUser_IdOrderByExpenseDateDesc(userId.get());
            return expenses;
        }
        return new ArrayList<>();
    }

    public Map<String, Double> getUserExpensesByMonth(UserDetails principal) {
        Long userId = userRepository.findIdByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getUsername()));
        List<Object[]> rows = expensesRepository.findTop12ByAppUser_IdGroupedByMonth(userId);
        Map<String, Double> expensesByMonth = new LinkedHashMap<>();
        for (Object[] row : rows) {
            int monthNumber = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            expensesByMonth.put(Month.of(monthNumber).name(), total);
        }
        return expensesByMonth;
    }

    public List<BudgetAndSpendDTO> getSmallestBudgetRemainders(UserDetails principal) {
        Long userId = userRepository.findIdByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getUsername()));

        List<CategoryBudget> monthlyBudget = budgetRepository.getCategoryBudgetByAppUser_Id(userId);
        System.out.println("User Id: " + userId + " Monthly Budget: " + monthlyBudget);

        if (!monthlyBudget.isEmpty()) {
            YearMonth thisMonth = YearMonth.now();
            LocalDate start = thisMonth.atDay(1);
            LocalDate end = thisMonth.atEndOfMonth();

            List<Object[]> rows = expensesRepository.findExpensesPerCategoryByAppUser_IdAndThisMonth(userId, start, end);
            List<BudgetAndSpendDTO> budgetAndSpendDTOs = new ArrayList<>();
            for (Object[] row : rows) {
                String categoryName = (String) row[0];
                double budget = 0;
                for(CategoryBudget categoryBudget : monthlyBudget) {
                    if (categoryBudget.getCategory().getName().equals(categoryName)) {
                        budget = categoryBudget.getAmount();
                    }
                }
                double spend = (double) row[1];
                double remaining = budget - spend;
                BudgetAndSpendDTO result = new BudgetAndSpendDTO(categoryName, budget, spend, remaining);
                if (budget > 0) {
                    budgetAndSpendDTOs.add(result);
                }
            }
            BubbleSort.sort(budgetAndSpendDTOs);
            return budgetAndSpendDTOs;
        } else {
            return new ArrayList<>();
        }
    }
}
