package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request) {
        var user = new AppUser();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));
        user.setAuthority("ROLE_USER");

        userRepository.save(user);

    }

    record RegistrationRequest(String username, String email, String password) {
    }
}
