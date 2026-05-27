package be.crismartens.financetracker.dto;

import be.crismartens.financetracker.model.AccountInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountInfoDTO {
    private Long id;
    @JsonProperty(value = "first_name")
    private String firstName;
    @JsonProperty(value = "last_name")
    private String lastName;
    private String country;
    @JsonProperty(value = "monthly_income")
    private Double monthlyIncome;

    public AccountInfoDTO() {}

    public AccountInfoDTO(AccountInfo account) {
        this.id = account.getId();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.country = account.getCountry();
        this.monthlyIncome = account.getMonthlyIncome();
    }
}
