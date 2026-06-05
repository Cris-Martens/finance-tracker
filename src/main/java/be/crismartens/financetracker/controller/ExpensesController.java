package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.Expense;
import be.crismartens.financetracker.dto.ExpenseDTO;
import be.crismartens.financetracker.service.ExpensesService;
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
@Tag(name = "Expenses", description = "Operations related to tracking expenses")
public class ExpensesController {
    private final ExpensesService expensesService;

    @Autowired
    public ExpensesController(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @Operation(
            summary = "Get all user expenses",
            description = "Provide the user with an overview of all tracked expenses"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully get all user expenses",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ExpenseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    @GetMapping("/user/expenses")
    public ResponseEntity<List<ExpenseDTO>> getExpenses(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.OK).body(expensesService.getExpensesByAppUserId(user));
    }

    @Operation(
            summary = "Get expense by id",
            description = "Allows the user to view one specific expense"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully GET expense by it's id",
                    content = @Content(schema = @Schema(implementation = ExpenseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Expense not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "User does not own specified expense",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/user/expenses/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable long id, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.OK).body(expensesService.getExpenseById(id, user));
    }

    @Operation(
            summary = "Create new expense",
            description = "Allow users to create new expenses"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Expense successfully created",
                    content = @Content(schema = @Schema(implementation = ExpenseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Specified category not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/user/expenses")
    public ResponseEntity<ExpenseDTO> addExpense(
            @RequestBody Expense expense,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expensesService.addExpense(expense, principal));
    }

    @Operation(
            summary = "Update existing expense",
            description = "Allows users to update a specific expense they are tracking"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expense updated successfully",
                    content = @Content(schema = @Schema(implementation = ExpenseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Expense not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Specified category not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "User is not owner of the expense",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/user/expenses")
    public ResponseEntity<ExpenseDTO> updateExpense(
            @RequestBody Expense expense,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(expensesService.updateExpense(expense, principal));
    }

    @Operation(
            summary = "Delete expense by id",
            description = "Allows users to delete tracked expenses"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Expense successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Expense not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "User does not own expense",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/user/expenses")
    public ResponseEntity<Void> deleteExpense(
            @RequestBody Expense expense,
            @AuthenticationPrincipal UserDetails principal) {
        expensesService.deleteExpense(expense, principal);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
