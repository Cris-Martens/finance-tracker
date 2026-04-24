package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.Expense;
import be.crismartens.financetracker.repository.ExpensesRepository;
import be.crismartens.financetracker.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ExpensesController {
    private final ExpensesService expensesService;

    @Autowired
    public ExpensesController(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @GetMapping("/user/expenses/{userId}")
    public List<Expense> getExpenses(@PathVariable Long userId) {
        return expensesService.getExpensesByUserId(userId);
    }

    @PostMapping("/user/expenses")
    public ResponseEntity<Void> addExpense(@RequestBody Expense expense, @AuthenticationPrincipal UserDetails principal) {
        System.out.println("Adding expense " + expense.getId());
        expensesService.addExpense(expense, principal);
        return ResponseEntity.ok().build();
    }
}
