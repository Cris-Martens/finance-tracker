package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.AccountInfo;
import be.crismartens.financetracker.dto.AccountInfoDTO;
import be.crismartens.financetracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/accountinfo")
    public ResponseEntity<AccountInfoDTO> addAccountInfo(@RequestBody AccountInfo accountInfo,
                                                                @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(accountService.addAccountInfo(accountInfo, principal));
    }

    @GetMapping("/accountinfo")
    public ResponseEntity<AccountInfoDTO> getAccountInfo(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccountInfo(principal));
    }

    @PutMapping("/accountinfo")
    public ResponseEntity<AccountInfoDTO> updateAccountInfo(@RequestBody AccountInfo accountInfo,
                                                                   @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.updateAccountInfo(accountInfo, principal));
    }

    @DeleteMapping("/accountinfo")
    public ResponseEntity<Void> deleteAccountInfo(@AuthenticationPrincipal UserDetails principal) {
        accountService.deleteAccountInfo(principal);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
