package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.repository.UserRepository;
import be.crismartens.financetracker.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserDetailsService userDetailsService;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder encoder, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegistrationRequest request) {
        var user = new AppUser();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));
        user.setAuthority("ROLE_USER");

        userRepository.save(user);

        return ResponseEntity.ok(new UserResponse("User added!", user.getUsername()));
    }

    record RegistrationRequest(String username, String email, String password) {}
}
