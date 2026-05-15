package be.crismartens.financetracker.unittesting.service;

import be.crismartens.financetracker.model.AppUser;
import be.crismartens.financetracker.service.UserValidationService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationServiceTest {
    static UserValidationService userValidationService = new UserValidationService();

    static String[] emails;
    static String[] passwords;
    static boolean[] expectedOutcomesIsValid;
    static boolean[] expectedOutcomesEmail;
    static boolean[] expectedOutcomesPassword;

    static int index = 0;

    static AppUser user = new AppUser();
    static boolean expectedIsValid;
    static boolean expectedEmail;
    static boolean expectedPassword;

    @BeforeAll
    static void setup() {
        emails = new String[] {"markg@outlook.com", "john@google.com", "Francesca",
                "mary@l@gmail.com", "tom@gmail.com", "test@example.com"};
        passwords = new String[] {"!8aDr-Ld&5ds", "@kd0Lm-dls!A4", "8&3dlS-D4L3D!s", "pass2", "passWord", "ValidPass123!"};
        expectedOutcomesIsValid = new boolean[] {true, true, false, false, false, true};
        expectedOutcomesEmail = new boolean[] {true, true, false, false, true, true};
        expectedOutcomesPassword = new boolean[] {true, true, true, false, false, true};
     }

     @BeforeEach
     void createUser(){
        user.setUsername(emails[index]);
        user.setPassword(passwords[index]);
        expectedIsValid = expectedOutcomesIsValid[index];
        expectedEmail = expectedOutcomesEmail[index];
        expectedPassword = expectedOutcomesPassword[index];
     }

     @AfterEach
     void increaseIndex(){
        index++;
     }

    @RepeatedTest(value = 2, name = "user is valid {currentRepetition}/{totalRepetitions}")
    void isUserValid() {
        assertEquals(expectedIsValid, userValidationService.isValid(user));
    }

    @RepeatedTest(value = 2, name = "check valid email {currentRepetition}/{totalRepetitions}")
    void checkValidEmail() {
        assertEquals(expectedEmail, userValidationService.validateEmail(user));
    }

    @RepeatedTest(value = 2, name = "Strong Password Check {currentRepetition}/{totalRepetitions}")
    void checkStrongPassword() {
        assertEquals(expectedPassword, userValidationService.strongPasswordCheck(user));
    }
}