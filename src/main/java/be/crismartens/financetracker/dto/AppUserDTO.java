package be.crismartens.financetracker.dto;

import be.crismartens.financetracker.model.AppUser;

public class AppUserDTO {
    private String username;

    public AppUserDTO(AppUser appUser) {
        this.username = appUser.getUsername();
    }
}
