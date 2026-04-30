package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.ExpenseDTO;
import be.crismartens.financetracker.repository.CategoryRepository;
import be.crismartens.financetracker.repository.ExpensesRepository;
import be.crismartens.financetracker.repository.UserRepository;
import be.crismartens.financetracker.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DashboardController {
    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/latest-expenses")
    public List<ExpenseDTO> listLatestExpenses(@AuthenticationPrincipal UserDetails principal) {
        return dashboardService.getLatestExpensesByAppUserId(principal);
    }

    @GetMapping("/dashboard/expenses-by-month")
    public Map<String, Double> ExpensesByMonth(@AuthenticationPrincipal UserDetails principal) {
        return dashboardService.getUserExpensesByMonth(principal);
    }
}

