package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ExpensesController {
    private final ExpensesService expensesService;

    @Autowired
    public ExpensesController(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @GetMapping("/user/expenses")
    public List<Expense> getExpenses(@AuthenticationPrincipal UserDetails user) {
        return expensesService.getExpensesByUserId(user);
    }

    @GetMapping("/user/expenses/{id}")
    public Optional<Expense> getExpenseById(@PathVariable long id, @AuthenticationPrincipal UserDetails user) {
        return expensesService.getExpenseById(id, user);
    }

    @PostMapping("/user/expenses")
    public ResponseEntity<Void> addExpense(
            @RequestBody Expense expense,
            @AuthenticationPrincipal UserDetails principal) {
        expensesService.addExpense(expense, principal);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/expenses")
    public ResponseEntity<Void> updateExpense(
            @RequestBody Expense expense,
            @AuthenticationPrincipal UserDetails principal) {
        expensesService.updateExpense(expense, principal);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/expenses")
    public ResponseEntity<Void> deleteExpense(
            @RequestBody Expense expense,
            @AuthenticationPrincipal UserDetails principal) {
        expensesService.deleteExpense(expense, principal);
        return ResponseEntity.ok().build();
    }
}
