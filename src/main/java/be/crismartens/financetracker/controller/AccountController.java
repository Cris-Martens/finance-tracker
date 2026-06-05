package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.AccountInfo;
import be.crismartens.financetracker.dto.AccountInfoDTO;
import be.crismartens.financetracker.service.AccountService;
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
@Tag(name = "Account Info", description = "Operations related to additional account info")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Add user Account info",
            description = "Creates new account info record for user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account info created",
                    content = @Content(schema = @Schema(implementation = AccountInfoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/accountinfo")
    public ResponseEntity<AccountInfoDTO> addAccountInfo(@RequestBody AccountInfo accountInfo,
                                                                @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(accountService.addAccountInfo(accountInfo, principal));
    }

    @Operation(
            summary = "View user Account info",
            description = "fetch the user's account info if it exists"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "GET account info success",
                    content = @Content(schema = @Schema(implementation = AccountInfoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/accountinfo")
    public ResponseEntity<AccountInfoDTO> getAccountInfo(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccountInfo(principal));
    }

    @Operation(
            summary = "Update user Account info",
            description = "Allows users the alter their account info"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account info successfully updated",
                    content = @Content(schema = @Schema(implementation = AccountInfoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "User Account info not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/accountinfo")
    public ResponseEntity<AccountInfoDTO> updateAccountInfo(@RequestBody AccountInfo accountInfo,
                                                                   @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.updateAccountInfo(accountInfo, principal));
    }

    @Operation(
            summary = "Delete user Account info",
            description = "Allows users to delete the record of their account info"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account info successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Account info not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/accountinfo")
    public ResponseEntity<Void> deleteAccountInfo(@AuthenticationPrincipal UserDetails principal) {
        accountService.deleteAccountInfo(principal);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
