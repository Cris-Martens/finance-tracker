package be.crismartens.financetracker.service;

import be.crismartens.financetracker.dto.BudgetAndSpendDTO;

import java.util.List;

public class BubbleSort {
    public static void sort(List<BudgetAndSpendDTO> budgets) {
        boolean swapped = true;

        while (swapped) {
            swapped = false;
            for (int i = 0; i < budgets.size() - 2; i++) {
                if (budgets.get(i + 1).getRemaining() <= budgets.get(i).getRemaining()) {

                    BudgetAndSpendDTO temp = budgets.get(i + 1);
                    budgets.set(i + 1, budgets.get(i));
                    budgets.set(i, temp);

                    swapped = true;
                }
            }
        }
    }
}
