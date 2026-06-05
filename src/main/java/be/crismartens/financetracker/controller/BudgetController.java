package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.dto.BudgetAndSpendDTO;
import be.crismartens.financetracker.dto.CategoryBudgetDTO;
import be.crismartens.financetracker.model.BudgetRequestBody;
import be.crismartens.financetracker.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Monthly budgets", description = "Operations related to setting monthly budget per category")
public class BudgetController {
    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @Operation(
            summary = "Create a new budget",
            description = "Allows user to create a new monthly budget for a given category"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "New budget successfully created",
                    content = @Content(schema = @Schema(implementation = CategoryBudgetDTO.class))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "Amount must be higher than 0",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Category does not exist",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/budget")
    public ResponseEntity<CategoryBudgetDTO> insertBudgets(@AuthenticationPrincipal UserDetails principal,
                                                           @RequestBody BudgetRequestBody budget) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.saveBudgets(principal, budget));
    }

    @Operation(
            summary = "Get list of budget",
            description = "Retrieve a list of all budgets set by the user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all budgets",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryBudgetDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/budget")
    public ResponseEntity<List<CategoryBudgetDTO>> listBudgets(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(budgetService.getAllBudgets(principal));
    }

    @Operation(
            summary = "Update user budgets",
            description = "Allows users to update the budgets they have set"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget successfully updated",
                    content = @Content(schema = @Schema(implementation = CategoryBudgetDTO.class))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "Budget must be higher than 0",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Category does not exist",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Budget not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/budget")
    public ResponseEntity<CategoryBudgetDTO> updateBudgets(@AuthenticationPrincipal UserDetails principal,
                              @RequestBody BudgetRequestBody budget) {
        return ResponseEntity.status(HttpStatus.OK).body(budgetService.updateBudgets(principal, budget));
    }

    @Operation(
            summary = "Delete monthly budget",
            description = "Allows users to delete a monthly budget for a specific category"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Budget successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "No category specified",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Monthly budget not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/budget/{category}")
    public ResponseEntity<Void> deleteBudgets(@AuthenticationPrincipal UserDetails principal, @PathVariable String category) {
        budgetService.deleteBudgets(principal, category);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
