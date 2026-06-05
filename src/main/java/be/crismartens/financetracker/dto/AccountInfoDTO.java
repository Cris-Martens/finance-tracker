package be.crismartens.financetracker.dto;

import be.crismartens.financetracker.model.AccountInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountInfoDTO {
    @Schema(
            description = "Long value identifier",
            example = "392"
    )
    private Long id;
    @JsonProperty(value = "first_name")
    @Schema(
            description = "User's first name",
            example = "John"
    )
    private String firstName;
    @JsonProperty(value = "last_name")
    @Schema(
            description = "User's last name",
            example = "Doe"
    )
    private String lastName;
    @Schema(
            description = "Country of residence",
            example = "Belgium"
    )
    private String country;
    @JsonProperty(value = "monthly_income")
    @Schema(
            description = "Monthly earning set by user",
            example = "2500"
    )
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
