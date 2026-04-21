package be.crismartens.financetracker.repository;

import be.crismartens.financetracker.model.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<AppUser> findByUsername(String username);
}
