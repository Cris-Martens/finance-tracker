package be.crismartens.financetracker.repository;

import be.crismartens.financetracker.model.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Query("""
            select c.id from Category c
                        where c.name = :categoryName
            """)
    Long findIdByName(@Param("categoryName")String categoryName);
}
