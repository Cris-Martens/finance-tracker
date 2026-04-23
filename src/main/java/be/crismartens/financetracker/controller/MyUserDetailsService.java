package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor injection
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user by username
     *
     * @param username is the username to search for
     * @return the UserDetails object
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> myUserOptional = userRepository.findByUsername(username);
        if (myUserOptional.isPresent()) {
            AppUser myUser = myUserOptional.get();
            return User.builder()
                    .username(myUser.getUsername())
                    .password(myUser.getPassword())
                    .roles(myUser.getAuthority().split(","))
                    .build();
        } else {
            throw new UsernameNotFoundException(username + "not found");
        }
    }
}