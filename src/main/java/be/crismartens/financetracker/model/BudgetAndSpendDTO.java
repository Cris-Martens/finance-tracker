package be.crismartens.financetracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BudgetAndSpendDTO {
    private String categoryName;
    private Double budget;
    private Double spend;
    private Double remaining;
}
