package be.crismartens.financetracker.service;

import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor injection
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> myUserOptional = userRepository.findByUsername(username);
        if (myUserOptional.isPresent()) {
            AppUser myUser = myUserOptional.get();
            return User.builder()
                    .username(myUser.getUsername())
                    .password(myUser.getPassword())
                    .authorities(myUser.getAuthority().split(","))
                    .build();
        } else {
            throw new UsernameNotFoundException(username + "not found");
        }
    }

    @Transactional
    public void deleteUser(UserDetails user) {
        Optional<AppUser> myUserOptional = userRepository.findByUsername(user.getUsername());
        if (myUserOptional.isPresent()) {
            userRepository.delete(myUserOptional.get());
        }
    }
}