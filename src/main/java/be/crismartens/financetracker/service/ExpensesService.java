package be.crismartens.financetracker.service;

import be.crismartens.financetracker.CategoryNotFoundException;
import be.crismartens.financetracker.ExpenseNotFoundException;
import be.crismartens.financetracker.UnauthorisedAccessException;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.model.Category;
import be.crismartens.financetracker.model.Expense;
import be.crismartens.financetracker.dto.ExpenseDTO;
import be.crismartens.financetracker.repository.CategoryRepository;
import be.crismartens.financetracker.repository.ExpensesRepository;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpensesService {
    private final ExpensesRepository expensesRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ExpensesService(ExpensesRepository expensesRepository,  UserRepository userRepository,
                           CategoryRepository categoryRepository) {
        this.expensesRepository = expensesRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public List<ExpenseDTO> getExpensesByAppUserId(UserDetails user) throws UsernameNotFoundException {
        // check if user exists
        Long userId = userRepository.findIdByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        // get expenses
        List<Expense> expenses = expensesRepository.findExpensesByAppUserId(userId);

        // return empty list when no expenses are found
        if (expenses.isEmpty()) {
            return new ArrayList<>();
        }

        // convert expense object to dto's
        List<ExpenseDTO> expensesDTO = new ArrayList<>();
        for (Expense expense : expenses) {
            expensesDTO.add(new ExpenseDTO(expense));
        }
        return expensesDTO;
    }

    public ExpenseDTO getExpenseById(long id, UserDetails user) throws UsernameNotFoundException, ExpenseNotFoundException {
        // check if user exists
        Long userId = userRepository.findIdByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(user.getUsername()));

        // check if expense exists
        Optional<Expense> expense = expensesRepository.findById(id);
        if (expense.isEmpty()) {
            throw new ExpenseNotFoundException(userId);
        }

        // check if user owns expense
        if (!userId.equals(expense.get().getAppUser().getId())) {
            throw new UnauthorisedAccessException("Unauthorised");
        }

        // get expense dto
        return new ExpenseDTO(expense.get());
    }

    public ExpenseDTO addExpense(Expense expense, UserDetails principal) {
        // check if user exists
        AppUser user = userRepository.findByUsername(principal.getUsername())
                        .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        // check if category exists
        Category category = categoryRepository.findById(expense.getCategory().getId())
                        .orElseThrow(() -> new CategoryNotFoundException("category not found"));

        // add user and category to expense
        expense.setAppUser(user);
        expense.setCategory(category);

        expensesRepository.save(expense);

        return new ExpenseDTO(expense);
    }

    @Transactional
    public ExpenseDTO updateExpense(Expense expense, UserDetails principal) throws ExpenseNotFoundException, UsernameNotFoundException {
        // Check User
        Long userId = userRepository.findIdByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        // check if Expense exists
        Expense updateExpense = expensesRepository.findById(expense.getId())
                .orElseThrow(() -> new ExpenseNotFoundException(expense.getId()));

        // Check if new category exists then overwrite based on id
        if (expense.getCategory() != null) {
            Category category = categoryRepository.findById(expense.getCategory().getId())
                    .orElseThrow(() -> new CategoryNotFoundException("category not found"));
            updateExpense.setCategory(category);
        }

        // Check if user owns expense
        if (!userId.equals(updateExpense.getAppUser().getId())) {
            throw new UnauthorisedAccessException("Unauthorised");
        }

        if (expense.getExpenseDate() != null) {
            updateExpense.setExpenseDate(expense.getExpenseDate());
        }
        if (expense.getAmount() != null) {
            updateExpense.setAmount(expense.getAmount());
        }
        if (expense.getDescription() != null) {
            updateExpense.setDescription(expense.getDescription());
        }

        expensesRepository.save(updateExpense);

        return new ExpenseDTO(updateExpense);
    }

    public void deleteExpense(Expense expense, UserDetails principal) {
        // check if user exists
        Long userId = userRepository.findIdByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        // check if expense exists
        Expense deleteExpense = expensesRepository.findById(expense.getId())
                .orElseThrow(() -> new ExpenseNotFoundException(expense.getId()));

        // check if user owns expense
        if (!userId.equals(deleteExpense.getAppUser().getId())) {
            throw new UnauthorisedAccessException("Unauthorised");
        }

        // delete expense
        expensesRepository.delete(expense);
    }
}
