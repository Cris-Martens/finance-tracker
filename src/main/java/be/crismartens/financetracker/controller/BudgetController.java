package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.CategoryBudget;
import be.crismartens.financetracker.model.CategoryBudgetDTO;
import be.crismartens.financetracker.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class BudgetController {
    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping("/budget")
    public void insertBudgets(@AuthenticationPrincipal UserDetails principal,
                              @RequestBody Map<String, Double> budgets) {
        budgetService.saveBudgets(principal, budgets);
    }

    @GetMapping("/budget")
    public Map<String, Double> listBudgets(@AuthenticationPrincipal UserDetails principal) {
        return budgetService.getAllBudgets(principal);
    }
}
