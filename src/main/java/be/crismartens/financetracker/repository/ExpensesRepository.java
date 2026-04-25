package be.crismartens.financetracker.repository;

import be.crismartens.financetracker.model.Expense;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ExpensesRepository extends CrudRepository<Expense, Long> {
    List<Expense> findAllExpensesByAppUserId(Long userId);
    Optional<Expense> findByIdAndAppUserUsername(Long Id, String username);
}
