package be.crismartens.financetracker.service;

import be.crismartens.financetracker.model.AppUser;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserValidationService {
    private static final Pattern STRONG_PASSWORD = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S{8,}$"
    );

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public boolean isValid(AppUser appUser) {
        if (strongPasswordCheck(appUser) && validateEmail(appUser)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validateEmail(AppUser appUser) {
        String email = appUser.getEmail();
        if (appUser.getEmail() == null || appUser.getEmail().isBlank()) {
            return false;
        } else if (!appUser.getEmail().contains("@")) {
            return false;
        } else return EMAIL.matcher(email).matches();
    }

    public boolean strongPasswordCheck(AppUser appUser) {
        String password = appUser.getPassword();
        if (password != null
                && !password.isBlank()
                && STRONG_PASSWORD.matcher(password).matches()) {
            return true;
        }
        return false;
    }
}
