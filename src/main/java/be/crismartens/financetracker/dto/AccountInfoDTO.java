package be.crismartens.financetracker.dto;

import be.crismartens.financetracker.model.AccountInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountInfoDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String country;
    private BigDecimal monthlyIncome;

    public AccountInfoDTO() {}

    public AccountInfoDTO(AccountInfo account) {
        this.id = account.getId();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.country = account.getCountry();
        this.monthlyIncome = account.getMonthlyIncome();
    }
}
