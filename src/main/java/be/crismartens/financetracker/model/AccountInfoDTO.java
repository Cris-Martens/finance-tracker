package be.crismartens.financetracker.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountInfoDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String country;

    public AccountInfoDTO() {}

    public AccountInfoDTO(AccountInfo account) {
        this.id = account.getId();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.country = account.getCountry();
    }
}
