package be.crismartens.financetracker.repository;

import be.crismartens.financetracker.model.Expense;
import be.crismartens.financetracker.model.ExpenseDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.nio.file.attribute.UserPrincipal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ExpensesRepository extends CrudRepository<Expense, Long> {
    // @Query("select e from Expense e join fetch e.category where e.appUser.id = :userId")
    List<Expense> findExpensesByAppUserId( //@Param("user_id")
                                                                                 Long userId);
    Optional<Expense> findByIdAndAppUserUsername(Long Id, String username);

    @Query("""
            select e from Expense e join fetch e.category where 
            e.appUser.id = :userId order by e.expenseDate desc limit 5
                        """)
    List<ExpenseDTO> findTop5ByAppUser_IdOrderByExpenseDateDesc(@Param("userId") Long userId);

    @Query("""
            select function('MONTH', e.expenseDate) as month,
                        sum(e.amount) as total
            from Expense e
            where e.appUser.id = :userId
            group by function('MONTH', e.expenseDate)
            order by function('MONTH', e.expenseDate)
            limit 12
            """)
    List<Object[]> findTop12ByAppUser_IdGroupedByMonth(@Param("userId") Long userId);

    @Query("""
            select e.category.name as caterogy,
                        SUM(e.amount) as total
            from Expense e
            where e.appUser.id = :userId
            and e.expenseDate between :start and :end
            group by e.category
            Order by e.category.id
            """)
    List<Object[]> findExpensesPerCategoryByAppUser_IdAndThisMonth(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
