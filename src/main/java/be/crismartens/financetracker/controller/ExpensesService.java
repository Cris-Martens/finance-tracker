package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.model.Expense;
import be.crismartens.financetracker.repository.ExpensesRepository;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpensesService {
    private final ExpensesRepository expensesRepository;
    private final UserRepository userRepository;

    public ExpensesService(ExpensesRepository expensesRepository,  UserRepository userRepository) {
        this.expensesRepository = expensesRepository;
        this.userRepository = userRepository;
    }

    public List<Expense> getExpensesByUserId(Long user_id) {
        return expensesRepository.findAllExpensesByUserId(user_id);
    }

    public void addExpense(Expense expense, UserDetails principal) {
        Optional<AppUser> user = userRepository.findByUsername(principal.getUsername());
        user.ifPresent(appUser -> expense.setUserId(appUser.getId()));

        expensesRepository.save(expense);
    }
}
