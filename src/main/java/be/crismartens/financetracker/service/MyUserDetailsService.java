package be.crismartens.financetracker.service;

import be.crismartens.financetracker.InvalidUserException;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.repository.UserRepository;
import be.crismartens.financetracker.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserValidationService userValidationService;

    // Constructor injection
    public MyUserDetailsService(UserRepository userRepository,
                                UserValidationService userValidationService) {
        this.userRepository = userRepository;
        this.userValidationService = userValidationService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> myUserOptional = userRepository.findByUsername(username);
        if (myUserOptional.isPresent()) {
            AppUser myUser = myUserOptional.get();
            return User.builder()
                    .username(myUser.getUsername())
                    .password(myUser.getPassword())
                    .authorities(myUser.getAuthority().split(","))
                    .build();
        } else {
            throw new UsernameNotFoundException(username + "not found");
        }
    }

    @Transactional
    public void deleteUser(UserDetails user) {
        Optional<AppUser> myUserOptional = userRepository.findByUsername(user.getUsername());
        if (myUserOptional.isPresent()) {
            userRepository.delete(myUserOptional.get());
        }
    }

    public ResponseEntity<UserResponse> registerUser(AppUser user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        if (!userValidationService.isValid(user)) {
            throw new InvalidUserException("Invalid email or weak password");
        }
        var appUser = new AppUser();
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        appUser.setUsername(user.getUsername());
        appUser.setEmail(user.getEmail());
        appUser.setPassword(encoder.encode(user.getPassword()));
        appUser.setAuthority("ROLE_USER");

        userRepository.save(appUser);

        return ResponseEntity.ok(new UserResponse("New User saved: ", appUser.getUsername()));
    }
}