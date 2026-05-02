package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.repository.UserRepository;
import be.crismartens.financetracker.response.UserResponse;
import be.crismartens.financetracker.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserRepository userRepository;
    private final MyUserDetailsService userDetailsService;

    @Autowired
    public UserController(UserRepository userRepository,
                          PasswordEncoder encoder,
                          MyUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody AppUser user) {
        return userDetailsService.registerUser(user);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@AuthenticationPrincipal UserDetails user) {
        userDetailsService.deleteUser(user);
    }
}
