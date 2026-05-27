package be.crismartens.financetracker.dto;

import be.crismartens.financetracker.model.AppUser;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AppUserDTO {
    @JsonProperty(value = "username")
    private String username;

    public AppUserDTO(AppUser appUser) {
        this.username = appUser.getUsername();
    }
}
