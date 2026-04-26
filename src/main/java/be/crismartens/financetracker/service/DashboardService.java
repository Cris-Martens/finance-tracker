package be.crismartens.financetracker.service;

import be.crismartens.financetracker.model.ExpenseDTO;
import be.crismartens.financetracker.repository.CategoryRepository;
import be.crismartens.financetracker.repository.ExpensesRepository;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<ExpenseDTO> getLatestExpensesByAppUserId(UserDetails user) {
        Optional<Long> userId = userRepository.findIdByUsername(user.getUsername());
        if(userId.isPresent()) {
            List<ExpenseDTO> expenses = expensesRepository
                    .findTop5ByAppUser_IdOrderByExpenseDateDesc(userId.get());
            return expenses;
        }
        return new ArrayList<>();
    }
}
