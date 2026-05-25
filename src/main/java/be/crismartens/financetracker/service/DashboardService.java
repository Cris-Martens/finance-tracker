package be.crismartens.financetracker.service;

import be.crismartens.financetracker.NoIncomeAddedException;
import be.crismartens.financetracker.dto.BudgetAndSpendDTO;
import be.crismartens.financetracker.model.Category;
import be.crismartens.financetracker.model.CategoryBudget;
import be.crismartens.financetracker.dto.ExpenseDTO;
import be.crismartens.financetracker.repository.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class DashboardService {
    private final UserRepository userRepository;
    private final ExpensesRepository expensesRepository;
    private final BudgetRepository budgetRepository;
    private final AccountRepository accountRepository;

    public DashboardService(UserRepository userRepository,
                            ExpensesRepository expensesRepository,
                            BudgetRepository budgetRepository,
                            AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.expensesRepository = expensesRepository;
        this.budgetRepository = budgetRepository;
        this.accountRepository = accountRepository;
    }

    public List<ExpenseDTO> getLatestExpensesByAppUserId(UserDetails principal) {
        Long userId = userRepository.findIdByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getUsername()));
        return expensesRepository
                .findTop5ByAppUser_IdOrderByExpenseDateDesc(userId);
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

            List<BudgetAndSpendDTO> budgetAndSpendDTOs = new ArrayList<>();

            for  (CategoryBudget categoryBudget : monthlyBudget) {
                String categoryName = categoryBudget.getCategory().getName();
                double budget = categoryBudget.getAmount();
                BudgetAndSpendDTO budgetAndSpendDTO = new BudgetAndSpendDTO(categoryName, budget, 0.0, budget);
                budgetAndSpendDTOs.add(budgetAndSpendDTO);
            }

            List<Object[]> rows = expensesRepository.findExpensesPerCategoryByAppUser_IdAndThisMonth(userId, start, end);
            for  (Object[] row : rows) {
                Object categoryObj = row[0];
                Category category = (Category) categoryObj;
                String categoryName = category.getName();

                for (BudgetAndSpendDTO budgetAndSpendDTO : budgetAndSpendDTOs) {
                    if (categoryName.equals(budgetAndSpendDTO.getCategory())) {
                        budgetAndSpendDTO.setSpend((double) row[1]);
                        budgetAndSpendDTO.setRemaining(
                                budgetAndSpendDTO.getBudget() -
                                        budgetAndSpendDTO.getSpend()
                        );
                        break;
                    }
                }
            }
            BubbleSort.sort(budgetAndSpendDTOs);
            return budgetAndSpendDTOs;
        } else {
            return new ArrayList<>();
        }
    }

    public Double getSavedAmount(UserDetails principal) throws ExecutionException, InterruptedException {
        Long userId = userRepository.findIdByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getUsername()));

        Double income = accountRepository.findMonthlyIncomeByAppUser_Id(userId);
        if (income != null) {
            YearMonth thisMonth = YearMonth.now();
            LocalDate start = thisMonth.atDay(1);
            LocalDate end = thisMonth.atEndOfMonth();

            Double totalSpend = expensesRepository.getSumExpensesByAppUser_IdAndMonth(userId, start, end);

            return income - totalSpend;
        } else  {
            throw new NoIncomeAddedException(userId);
        }
    }
}
