package be.crismartens.financetracker.service;

import be.crismartens.financetracker.ExpenseNotFoundException;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.model.Expense;
import be.crismartens.financetracker.model.ExpenseDTO;
import be.crismartens.financetracker.repository.ExpensesRepository;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ExpensesService {
    private final ExpensesRepository expensesRepository;
    private final UserRepository userRepository;

    public ExpensesService(ExpensesRepository expensesRepository,  UserRepository userRepository) {
        this.expensesRepository = expensesRepository;
        this.userRepository = userRepository;
    }

    public List<ExpenseDTO> getExpensesByAppUserId(UserDetails user) throws UsernameNotFoundException {
        Optional<Long> userId = userRepository.findIdByUsername(user.getUsername());
        System.out.println("Attempt to use userId");
        if  (userId.isPresent()) {
            List<Expense> expenses = expensesRepository.findAllExpensesByAppUserId(userId.get());
            List<ExpenseDTO> expensesDTO = new ArrayList<>();
            for (Expense expense : expenses) {
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
        if (userId.isEmpty()) {
            throw new UsernameNotFoundException("User: " + principal.getUsername() + " not found");
        }
        Expense updateExpense = expensesRepository.findById(expense.getId()).get();
        if (userId.get().equals(updateExpense.getAppUser().getId())) {
            updateExpense.setExpenseDate(expense.getExpenseDate());
            updateExpense.setCategoryId(expense.getCategoryId());
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
