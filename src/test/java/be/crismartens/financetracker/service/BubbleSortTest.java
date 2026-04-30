package be.crismartens.financetracker.service;

import be.crismartens.financetracker.model.BudgetAndSpendDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BubbleSortTest {

    @Test
    void firstSort() {
        List<BudgetAndSpendDTO> budget = new ArrayList<>();
        budget.add(new BudgetAndSpendDTO("Housing", 700, 500, 200));
        budget.add(new BudgetAndSpendDTO("Utilities", 200, 190, 10));
        budget.add(new BudgetAndSpendDTO("Transportation", 50, 10, 40));
        budget.add(new BudgetAndSpendDTO("Entertainment", 150, 0, 150));
        budget.add(new BudgetAndSpendDTO("Clothes", 130, 140, -10));
        budget.add(new BudgetAndSpendDTO("Car", 810, 450, 360));

        BubbleSort.sort(budget);

        List<BudgetAndSpendDTO> expected = new ArrayList<>();
        expected.add(new BudgetAndSpendDTO("Clothes", 130, 140, -10));
        expected.add(new BudgetAndSpendDTO("Utilities", 200, 190, 10));
        expected.add(new BudgetAndSpendDTO("Transportation", 50, 10, 40));
        expected.add(new BudgetAndSpendDTO("Entertainment", 150, 0, 150));
        expected.add(new BudgetAndSpendDTO("Housing", 700, 500, 200));
        expected.add(new BudgetAndSpendDTO("Car", 810, 450, 360));

        assertEquals(expected, budget);
    }
}