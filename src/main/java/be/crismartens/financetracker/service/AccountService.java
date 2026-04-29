package be.crismartens.financetracker.service;

import be.crismartens.financetracker.model.AccountInfo;
import be.crismartens.financetracker.model.AccountInfoDTO;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.repository.AccountRepository;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
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

    public void upsertAccountInfo(AccountInfo accountInfo, UserDetails principal) {
        AppUser user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        Optional<AccountInfo> updatingAccountInfo = accountRepository.findByAppUser_Id(user.getId());
        if (updatingAccountInfo.isPresent()) {
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
        } else {
            System.out.println(accountInfo.getMonthlyIncome());
            accountInfo.setAppUser(user);

            accountRepository.save(accountInfo);
        }
    }

    public AccountInfoDTO getAccountInfo(UserDetails principal) {
        Long userId = userRepository.findIdByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        Optional<AccountInfo> accountInfo = accountRepository.findByAppUser_Id(userId);
        if (accountInfo.isPresent()) {
            return new AccountInfoDTO(accountInfo.get());
        } else {
            return new AccountInfoDTO();
        }
    }

    public void deleteAccountInfo(UserDetails principal) {
        Long userId = userRepository.findIdByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        Optional<AccountInfo> accountInfo = accountRepository.findByAppUser_Id(userId);
        if (accountInfo.isPresent()) {
            accountRepository.delete(accountInfo.get());
        }
    }
}
