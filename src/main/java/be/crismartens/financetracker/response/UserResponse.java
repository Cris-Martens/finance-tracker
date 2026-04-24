package be.crismartens.financetracker.response;

import org.springframework.context.annotation.Bean;

public class UserResponse {
    private String message;
    private String username;

    public UserResponse(String message, String username) {
        this.message = message;
        this.username = username;
    }

    @Bean
    public String getMessage() {
        return message;
    }
    @Bean
    public void setMessage(String message) {
        this.message = message;
    }
    @Bean
    public String getUsername() {
        return username;
    }
    @Bean
    public void setUsername(String username) {
        this.username = username;
    }
}
