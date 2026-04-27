package be.crismartens.financetracker.repository;

import be.crismartens.financetracker.model.CategoryBudget;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends CrudRepository<CategoryBudget, Long> {

    @Query("""
            select b from CategoryBudget b join fetch b.category
                        where b.appUser.id = :userId
                                    order by b.id
            """)
    List<CategoryBudget> getCategoryBudgetByAppUser_Id(@Param("userId")Long userId);

    @Query("""
            select b from CategoryBudget b
                        where b.appUser.id = :userId
                                    and b.category.id = :categoryId
            """)
    CategoryBudget getCategoryBudgetByAppUser_IdAndCategory_Id(@Param("userId")Long userId, @Param("categoryId")Long categoryId);
}
