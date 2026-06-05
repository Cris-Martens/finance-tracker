package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.dto.BudgetAndSpendDTO;
import be.crismartens.financetracker.dto.ExpenseDTO;
import be.crismartens.financetracker.service.DashboardService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Dashboard", description = "Operations related to spending analytics and insights")
public class DashboardController {
    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(
            summary = "Fetch latest expenses",
            description = "Get a list of the user's five latest expense"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expenses successfully retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ExpenseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
    })
    @GetMapping("/dashboard/latest-expenses")
    public ResponseEntity<List<ExpenseDTO>> listLatestExpenses(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getLatestExpensesByAppUserId(principal));
    }

    @Operation(
            summary = "List total expenses by month",
            description = "Allows users to get an overview of how much they've spend each month"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Overview expenses by month successful",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/dashboard/expenses-by-month")
    public ResponseEntity<Map<String, Double>> ExpensesByMonth(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getUserExpensesByMonth(principal));
    }

    @Operation(
            summary = "List categories close to exceeding budget",
            description = "Allows the user to view a list of the categories to are closest to or have already exceeded it's set budget"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget comparisons loaded successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BudgetAndSpendDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/dashboard/category-budget-left")
    public ResponseEntity<List<BudgetAndSpendDTO>> BudgetLeft(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getSmallestBudgetRemainders(principal));
    }

    @Operation(
            summary = "View amount not spend",
            description = "Allows the user to see the number of how much of his/her monthly income hasn't been spend yet"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "GET saved amount successfully",
                    content = @Content(schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "404", description = "Username not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "No monthly income found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/dashboard/totalsaved")
    public ResponseEntity<Double> getTotalSavedAmount(@AuthenticationPrincipal UserDetails principal) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getSavedAmount(principal));
    }
}

