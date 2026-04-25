package be.crismartens.financetracker.repository;

import be.crismartens.financetracker.model.Expense;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExpensesRepository extends CrudRepository<Expense, Long> {
    public List<Expense> findAllExpensesByAppUserId(long userId);
}
