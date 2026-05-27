package be.crismartens.financetracker.auth;

import be.crismartens.financetracker.exceptions.InvalidUserException;
import be.crismartens.financetracker.exceptions.UsernameAlreadyInUseExcepion;
import be.crismartens.financetracker.dto.AppUserDTO;
import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.repository.UserRepository;
import be.crismartens.financetracker.service.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserValidationService userValidationService;

    // Constructor injection
    @Autowired
    public MyUserDetailsService(UserRepository userRepository,
                                UserValidationService userValidationService) {
        this.userRepository = userRepository;
        this.userValidationService = userValidationService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser myUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.builder()
                .username(myUser.getUsername())
                .password(myUser.getPassword())
                .authorities(myUser.getAuthority().split(","))
                .build();
    }

    @Transactional
    public void deleteUser(UserDetails user) {
        AppUser myUserOptional = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(user.getUsername()));

        userRepository.delete(myUserOptional);
    }

    public AppUserDTO registerUser(AppUser user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameAlreadyInUseExcepion(user.getUsername());
        }
        if (!userValidationService.isValid(user)) {
            throw new InvalidUserException();
        }
        AppUser appUser = new AppUser();
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        appUser.setUsername(user.getUsername());
        appUser.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(appUser);

        return new AppUserDTO(appUser);
    }
}