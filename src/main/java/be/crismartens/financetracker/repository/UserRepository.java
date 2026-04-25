package be.crismartens.financetracker.repository;

import be.crismartens.financetracker.model.AppUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CrudRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    @Query("select u.id from AppUser u where u.username = :username")
    Optional<Long> findIdByUsername(@Param("username") String username);
}
