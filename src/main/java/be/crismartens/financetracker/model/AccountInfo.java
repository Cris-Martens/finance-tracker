package be.crismartens.financetracker.model;

import be.crismartens.financetracker.config.EncryptedBigDecimalConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "account_info")
public class AccountInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String country;

    @JsonProperty("monthly_income")
    @Convert(converter = EncryptedBigDecimalConverter.class)
    @Column(name = "monthly_income", length = 255)
    private BigDecimal monthlyIncome;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser appUser;
}
