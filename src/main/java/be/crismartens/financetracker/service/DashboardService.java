package be.crismartens.financetracker.service;

import be.crismartens.financetracker.model.ExpenseDTO;
import be.crismartens.financetracker.repository.CategoryRepository;
import be.crismartens.financetracker.repository.ExpensesRepository;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.*;

@Service
public class DashboardService {
    private final UserRepository userRepository;
    private final ExpensesRepository expensesRepository;
    private final CategoryRepository categoryRepository;

    public DashboardService(UserRepository userRepository,
                            ExpensesRepository expensesRepository,
                            CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.expensesRepository = expensesRepository;
        this.categoryRepository = categoryRepository;
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
}
