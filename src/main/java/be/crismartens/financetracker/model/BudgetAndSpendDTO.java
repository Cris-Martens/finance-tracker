package be.crismartens.financetracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BudgetAndSpendDTO {
    private String category;
    private double budget;
    private double spend;
    private double remaining;
}
