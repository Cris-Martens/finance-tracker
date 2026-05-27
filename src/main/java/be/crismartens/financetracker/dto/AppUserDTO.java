package be.crismartens.financetracker.dto;

import be.crismartens.financetracker.model.AppUser;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class AppUserDTO {
    @JsonProperty(value = "username")
    private String username;

    public AppUserDTO(AppUser appUser) {
        this.username = appUser.getUsername();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AppUserDTO that = (AppUserDTO) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
