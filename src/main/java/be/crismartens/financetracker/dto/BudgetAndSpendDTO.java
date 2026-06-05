package be.crismartens.financetracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BudgetAndSpendDTO {
    @Schema(
            description = "Name of the category in question",
            example = "Housing"
    )
    private String category;
    @Schema(
            description = "Expected monthly expenses for this category",
            example = "750.00"
    )
    private double budget;
    @Schema(
            description = "Actual expenses for this category this month so far",
            example = "450.00"
    )
    private double spend;
    @Schema(
            description = "Difference between budgeted amount and spend amount",
            example = "300.00"
    )
    private double remaining;
}
