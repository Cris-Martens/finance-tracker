package be.crismartens.financetracker.service;

import be.crismartens.financetracker.CategoryNotFoundException;
import be.crismartens.financetracker.ExpenseNotFoundException;
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
import java.util.Objects;
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
        Optional<Long> userId = userRepository.findIdByUsername(user.getUsername());
        System.out.println("Attempt to use userId");
        if  (userId.isPresent()) {
            List<ExpenseDTO> expensesDTO = new ArrayList<>();
            List<Expense> expenses = expensesRepository.
                    findExpensesByAppUserId(userId.get());
            for (Expense expense: expenses) {
                expensesDTO.add(new ExpenseDTO(expense));
            }
            return expensesDTO;
        }
        return new ArrayList<>();
    }

    public ExpenseDTO getExpenseById(long id, UserDetails user) throws ExpenseNotFoundException, UsernameNotFoundException{
        Optional<Expense> expense = expensesRepository.findById(id);
        Optional<Long> userId = userRepository.findIdByUsername(user.getUsername());
        if (expense.isEmpty()) {
            throw new ExpenseNotFoundException("Expense with id " + id+ " not found" );
        }
        if (userId.isEmpty()) {
            throw new UsernameNotFoundException("User: " + user.getUsername() + " not found");
        }
        if (!userId.get().equals(expense.get().getAppUser().getId())) {
            throw new UsernameNotFoundException("User: " + user.getUsername() + " not found");
        }
        return new ExpenseDTO(expense.get());
    }

    public void addExpense(Expense expense, UserDetails principal) {
        Optional<AppUser> user = userRepository.findByUsername(principal.getUsername());
        user.ifPresent(expense::setAppUser);

        expensesRepository.save(expense);
    }

    public void updateExpense(Expense expense, UserDetails principal) throws ExpenseNotFoundException, UsernameNotFoundException {
        Optional<Long> userId = userRepository.findIdByUsername(principal.getUsername());
        Optional<Category> category = categoryRepository.findByName(expense.getCategory().getName());
        if (category.isEmpty()) {
            throw new CategoryNotFoundException(expense.getCategory().getName() + "not found.");
        }
        if (userId.isEmpty()) {
            throw new UsernameNotFoundException("User: " + principal.getUsername() + " not found");
        }
        Expense updateExpense = expensesRepository.findById(expense.getId()).get();
        if (userId.get().equals(updateExpense.getAppUser().getId())) {
            updateExpense.setExpenseDate(expense.getExpenseDate());
            updateExpense.setCategory(category.get());
            updateExpense.setAmount(expense.getAmount());
            updateExpense.setDescription(expense.getDescription());
            updateExpense.setAppUser(userRepository.findByUsername(principal.getUsername()).get());

            expensesRepository.save(updateExpense);
        }
    }

    public void deleteExpense(Expense expense, UserDetails principal) {
        Optional<AppUser> user = userRepository.findByUsername(principal.getUsername());
        if (user.isPresent()) {
            Expense deleteExpense = expensesRepository.findById(expense.getId()).get();
            if (Objects.equals(deleteExpense.getAppUser().getId(), user.get().getId())) {
                expensesRepository.delete(deleteExpense);
            }

        }
    }
}
