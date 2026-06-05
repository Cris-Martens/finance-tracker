package be.crismartens.financetracker.dto;

import be.crismartens.financetracker.model.AppUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

public class AppUserDTO {
    @JsonProperty(value = "username")
    @Schema(
            description = "User email as username",
            example = "johndoe@example.com"
    )
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
