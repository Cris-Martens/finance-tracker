package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.dto.AppUserDTO;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.auth.MyUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Users", description = "Operation for user creation and deletion")
public class UserController {
    private final MyUserDetailsService userDetailsService;

    @Autowired
    public UserController(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Operation(
            summary = "Create new user",
            description = "Allow for account creation to be able to use other services"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "New user successfully created",
                    content = @Content(schema = @Schema(implementation = AppUserDTO.class))),
            @ApiResponse(responseCode = "409", description = "Username already in use",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "Invalid username and/or weak password",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(path = "/register")
    public ResponseEntity<AppUserDTO> registerUser(@RequestBody AppUser user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userDetailsService.registerUser(user));
    }

    @Operation(
            summary = "Delete existing user",
            description = "Delete an existing user, and all information stored related to the user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetails user) {
        userDetailsService.deleteUser(user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
