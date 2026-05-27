package be.crismartens.financetracker.controller;

import be.crismartens.financetracker.dto.CategoryBudgetDTO;
import be.crismartens.financetracker.model.BudgetRequestBody;
import be.crismartens.financetracker.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BudgetController {
    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping("/budget")
    public ResponseEntity<CategoryBudgetDTO> insertBudgets(@AuthenticationPrincipal UserDetails principal,
                                                           @RequestBody BudgetRequestBody budget) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.saveBudgets(principal, budget));
    }

    @GetMapping("/budget")
    public ResponseEntity<List<CategoryBudgetDTO>> listBudgets(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.OK).body(budgetService.getAllBudgets(principal));
    }

    @PutMapping("/budget")
    public ResponseEntity<CategoryBudgetDTO> updateBudgets(@AuthenticationPrincipal UserDetails principal,
                              @RequestBody BudgetRequestBody budget) {
        return ResponseEntity.status(HttpStatus.OK).body(budgetService.updateBudgets(principal, budget));
    }

    @DeleteMapping("/budget/{category}")
    public ResponseEntity<Void> deleteBudgets(@AuthenticationPrincipal UserDetails principal, @PathVariable String category) {
        budgetService.deleteBudgets(principal, category);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
