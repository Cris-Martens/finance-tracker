package be.crismartens.financetracker.service;

import be.crismartens.financetracker.AccountInfoNotFoundException;
import be.crismartens.financetracker.model.AccountInfo;
import be.crismartens.financetracker.dto.AccountInfoDTO;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.repository.AccountRepository;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Long findAppUserId(UserDetails principal) {
        return userRepository.findIdByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public AccountInfoDTO updateAccountInfo(AccountInfo accountInfo, UserDetails principal) {
        Long userId = findAppUserId(principal);

        Optional<AccountInfo> updatingAccountInfo = accountRepository.findByAppUser_Id(userId);

        if (updatingAccountInfo.isEmpty()) {
            throw new AccountInfoNotFoundException(userId);
        }

        if (accountInfo.getFirstName() != null) {
            updatingAccountInfo.get().setFirstName(accountInfo.getFirstName());
        }
        if (accountInfo.getLastName() != null) {
            updatingAccountInfo.get().setLastName(accountInfo.getLastName());
        }
        if (accountInfo.getCountry() != null) {
            updatingAccountInfo.get().setCountry(accountInfo.getCountry());
        }
        if (accountInfo.getMonthlyIncome() != null) {
            updatingAccountInfo.get().setMonthlyIncome(accountInfo.getMonthlyIncome());
        }

        accountRepository.save(updatingAccountInfo.get());

        return new AccountInfoDTO(updatingAccountInfo.get());
    }

    public AccountInfoDTO addAccountInfo(AccountInfo accountInfo, UserDetails principal) {
        AppUser user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        accountInfo.setAppUser(user);

        return new AccountInfoDTO(accountInfo);
    }

    public AccountInfoDTO getAccountInfo(UserDetails principal) {
        Long userId = findAppUserId(principal);

        Optional<AccountInfo> accountInfo = accountRepository.findByAppUser_Id(userId);

        return accountInfo.map(AccountInfoDTO::new).orElseGet(AccountInfoDTO::new);
    }

    public AccountInfoDTO deleteAccountInfo(UserDetails principal) {
        Long userId = findAppUserId(principal);

        AccountInfo accountInfo = accountRepository.findByAppUser_Id(userId)
                .orElseThrow(() -> new AccountInfoNotFoundException(userId));

        accountRepository.delete(accountInfo);

        return new AccountInfoDTO(accountInfo);
    }
}
