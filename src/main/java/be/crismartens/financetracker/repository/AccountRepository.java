package be.crismartens.financetracker.repository;

import be.crismartens.financetracker.model.AccountInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<AccountInfo, Long> {

    @Query("""
            select a from AccountInfo a where a.appUser.id = :userId
            """)
    Optional<AccountInfo> findByAppUser_Id(@Param("userId") Long userId);
}
