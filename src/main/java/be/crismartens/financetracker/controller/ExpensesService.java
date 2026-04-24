package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.model.Expense;
import be.crismartens.financetracker.repository.ExpensesRepository;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<Expense> getExpensesByUserId(UserDetails user) throws UsernameNotFoundException {
        Optional<AppUser> appUser = userRepository.findByUsername(user.getUsername());
        if(appUser.isPresent()) {
            return expensesRepository.findAllExpensesByUserId(appUser.get().getId());
        }
        return new ArrayList<>();
    }

    public void addExpense(Expense expense, UserDetails principal) {
        Optional<AppUser> user = userRepository.findByUsername(principal.getUsername());
        user.ifPresent(appUser -> expense.setUserId(appUser.getId()));

        expensesRepository.save(expense);
    }

    public void updateExpense(Expense expense, UserDetails principal) {
        Optional<AppUser> user = userRepository.findByUsername(principal.getUsername());
        if (user.isPresent()) {
            Expense updateExpense = expensesRepository.findById(expense.getId()).get();
            if (updateExpense.getUserId().equals(user.get().getId())) {
                updateExpense.setCategoryId(expense.getCategoryId());
                updateExpense.setAmount(expense.getAmount());
                updateExpense.setDescription(expense.getDescription());

                expensesRepository.save(updateExpense);
            }
        }
    }

    public void deleteExpense(Expense expense, UserDetails principal) {
        Optional<AppUser> user = userRepository.findByUsername(principal.getUsername());
        if (user.isPresent()) {
            Expense deleteExpense = expensesRepository.findById(expense.getId()).get();
            if (deleteExpense.getUserId().equals(user.get().getId())) {
                expensesRepository.delete(deleteExpense);
            }

        }
    }
}
