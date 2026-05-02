package be.crismartens.financetracker.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BudgetAndSpendDTO {
    private String category;
    private double budget;
    private double spend;
    private double remaining;
}
