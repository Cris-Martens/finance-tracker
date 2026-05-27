package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.model.Expense;
import be.crismartens.financetracker.dto.ExpenseDTO;
import be.crismartens.financetracker.service.ExpensesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/user/expenses")
    public ResponseEntity<List<ExpenseDTO>> getExpenses(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.OK).body(expensesService.getExpensesByAppUserId(user));
    }

    @GetMapping("/user/expenses/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable long id, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.OK).body(expensesService.getExpenseById(id, user));
    }

    @PostMapping("/user/expenses")
    public ResponseEntity<ExpenseDTO> addExpense(
            @RequestBody Expense expense,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expensesService.addExpense(expense, principal));
    }

    @PutMapping("/user/expenses")
    public ResponseEntity<ExpenseDTO> updateExpense(
            @RequestBody Expense expense,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(expensesService.updateExpense(expense, principal));
    }

    @DeleteMapping("/user/expenses")
    public ResponseEntity<Void> deleteExpense(
            @RequestBody Expense expense,
            @AuthenticationPrincipal UserDetails principal) {
        expensesService.deleteExpense(expense, principal);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
