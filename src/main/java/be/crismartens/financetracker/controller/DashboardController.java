package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.dto.BudgetAndSpendDTO;
import be.crismartens.financetracker.dto.ExpenseDTO;
import be.crismartens.financetracker.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class DashboardController {
    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/latest-expenses")
    public ResponseEntity<List<ExpenseDTO>> listLatestExpenses(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getLatestExpensesByAppUserId(principal));
    }

    @GetMapping("/dashboard/expenses-by-month")
    public ResponseEntity<Map<String, Double>> ExpensesByMonth(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getUserExpensesByMonth(principal));
    }

    @GetMapping("/dashboard/category-budget-left")
    public ResponseEntity<List<BudgetAndSpendDTO>> BudgetLeft(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getSmallestBudgetRemainders(principal));
    }

    @GetMapping("/dashboard/totalsaved")
    public ResponseEntity<Double> getTotalSavedAmount(@AuthenticationPrincipal UserDetails principal) throws ExecutionException, InterruptedException {
        return ResponseEntity.status(HttpStatus.OK).body(dashboardService.getSavedAmount(principal));
    }
}

