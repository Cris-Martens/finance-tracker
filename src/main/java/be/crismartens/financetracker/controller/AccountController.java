package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.AccountInfo;
import be.crismartens.financetracker.dto.AccountInfoDTO;
import be.crismartens.financetracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void addAccountInfo(@RequestBody AccountInfo accountInfo,
                           @AuthenticationPrincipal UserDetails principal) {
        accountService.upsertAccountInfo(accountInfo, principal);
    }

    @GetMapping("/accountinfo")
    public AccountInfoDTO getAccountInfo(@AuthenticationPrincipal UserDetails principal) {
        return accountService.getAccountInfo(principal);
    }

    @PutMapping("/accountinfo")
    public void updateAccountInfo(@RequestBody AccountInfo accountInfo,
                              @AuthenticationPrincipal UserDetails principal) {
        accountService.upsertAccountInfo(accountInfo, principal);
    }

    @DeleteMapping("/accountinfo")
    public void deleteAccountInfo(@AuthenticationPrincipal UserDetails principal) {
        accountService.deleteAccountInfo(principal);
    }
}
