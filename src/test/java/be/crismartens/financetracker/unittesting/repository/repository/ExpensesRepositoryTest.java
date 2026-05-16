package be.crismartens.financetracker.unittesting.repository.repository;

import be.crismartens.financetracker.dto.ExpenseDTO;
import be.crismartens.financetracker.model.Expense;
import be.crismartens.financetracker.repository.ExpensesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql"})
class ExpensesRepositoryTest {

    @Autowired
    private ExpensesRepository expensesRepository;

    @Test
    @DisplayName("Get all user expenses")
    void findAllExpensesByUserId() {
        // Arrange

        // Act
        List<Expense> expenses = expensesRepository.findExpensesByAppUserId(1L);

        // Assert
        assertNotNull(expenses);
        assertEquals(6, expenses.size());
    }

    @Test
    @DisplayName("Get five latest user expenses")
    void findFiveLatestExpensesByUserId() {
        // Arrange
        LocalDate date1 = LocalDate.parse("2026-04-28");
        LocalDate date2 = LocalDate.parse("2026-04-19");
        LocalDate date3 = LocalDate.parse("2026-04-15");
        LocalDate date4 = LocalDate.parse("2026-03-24");
        LocalDate date5 = LocalDate.parse("2026-03-12");

        // Act
        List<ExpenseDTO> expenses = expensesRepository.findTop5ByAppUser_IdOrderByExpenseDateDesc(1L);

        // Assert
        assertNotNull(expenses);
        assertEquals(5, expenses.size());
        assertEquals(date1, expenses.get(0).getExpenseDate());
        assertEquals(date2, expenses.get(1).getExpenseDate());
        assertEquals(date3, expenses.get(2).getExpenseDate());
        assertEquals(date4, expenses.get(3).getExpenseDate());
        assertEquals(date5, expenses.get(4).getExpenseDate());
    }

    @Test
    @DisplayName("Get total amount spend by month last year")
    void findTotalAmountSpendByMonthLastYear() {
        // Arrange
        Double totalApril = 690.0 + 10.0 + 100.0;
        Double totalMarch = 70.0 + 180.0;
        Double totalFebruary = 30.0;

        // Act
        List<Object[]> spendByMonth = expensesRepository.findTop12ByAppUser_IdGroupedByMonth(1L);
        Map<String, Double> spendByMonthMap = new LinkedHashMap<>();
        for (Object[] obj : spendByMonth) {
            int monthNumber = ((Number) obj[0]).intValue();
            double total = ((Number) obj[1]).doubleValue();
            spendByMonthMap.put(Month.of(monthNumber).name(), total);
        }

        // Assert
        assertNotNull(spendByMonth);
        assertEquals(3, spendByMonth.size());
        assertEquals(totalApril, spendByMonthMap.get("APRIL"));
        assertEquals(totalMarch, spendByMonthMap.get("MARCH"));
        assertEquals(totalFebruary, spendByMonthMap.get("FEBRUARY"));
    }

    @Test
    @DisplayName("Get expenses per category for this month")
    void findExpensesByCategoryForThisMonth() {
        // Arrange
        LocalDate start = LocalDate.parse("2026-04-01");
        LocalDate end = LocalDate.parse("2026-04-30");

        Map<String, Double> spendByMonthMapExpected = new LinkedHashMap<>();
        spendByMonthMapExpected.put("Housing", 690.0);
        spendByMonthMapExpected.put("Groceries", 100.0);
        spendByMonthMapExpected.put("Transportation", 10.0);

        // Act
        List<Object[]> spendByMonth =
                expensesRepository.findExpensesPerCategoryByAppUser_IdAndThisMonth(1L, start, end);
        Map<String, Double> spendByMonthMap = new LinkedHashMap<>();
        for (Object[] obj : spendByMonth) {
            String categoryName = obj[0].toString();
            Double spend = (Double) obj[1];
            spendByMonthMap.put(categoryName, spend);
        }

        // Assert
        assertNotNull(spendByMonth);
        assertEquals(3, spendByMonth.size());
        assertEquals(spendByMonthMapExpected.get("Housing"), spendByMonthMap.get("Housing"));
        assertEquals(spendByMonthMapExpected.get("Groceries"), spendByMonthMap.get("Groceries"));
        assertEquals(spendByMonthMapExpected.get("Transportation"), spendByMonthMap.get("Transportation"));
    }

    @Test
    @DisplayName("Get Total expenses for this month")
    void findTotalExpensesForThisMonth() {
        // Arrange
        LocalDate start = LocalDate.parse("2026-04-01");
        LocalDate end = LocalDate.parse("2026-04-30");

        Double expected = 690.0 + 10.0 + 100.0;

        // Act
        Double result = expensesRepository.getSumExpensesByAppUser_IdAndMonth(1L, start, end);

        // Assert
        assertEquals(expected, result);
    }
}