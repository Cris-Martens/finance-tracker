package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.dto.AppUserDTO;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.response.UserResponse;
import be.crismartens.financetracker.auth.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final MyUserDetailsService userDetailsService;

    @Autowired
    public UserController(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<AppUserDTO> registerUser(@RequestBody AppUser user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userDetailsService.registerUser(user));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetails user) {
        userDetailsService.deleteUser(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
