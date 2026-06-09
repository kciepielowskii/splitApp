package com.splitapp.splitApp.service;

import com.splitapp.splitApp.dto.response.DebtResponse;
import com.splitapp.splitApp.model.Expense;
import com.splitapp.splitApp.model.ExpenseSplit;
import com.splitapp.splitApp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DebtCalculatorServiceTest {

    private DebtCalculatorService debtCalculatorService;
    private User konrad;
    private User anna;
    private User piotr;

    @BeforeEach
    void setUp() {
        debtCalculatorService = new DebtCalculatorService();

        konrad = User.builder().id(1L).displayName("Konrad").email("konrad@test.com").password("pass").build();
        anna = User.builder().id(2L).displayName("Anna").email("anna@test.com").password("pass").build();
        piotr = User.builder().id(3L).displayName("Piotr").email("piotr@test.com").password("pass").build();
    }

    @Test
    void shouldReturnEmptyListWhenNoExpenses() {
        List<DebtResponse> result = debtCalculatorService.calculate(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCalculateSimpleDebtBetweenTwoPeople() {
        Expense expense = createExpense(konrad, new BigDecimal("900.00"),
                List.of(
                        createSplit(konrad, new BigDecimal("450.00")),
                        createSplit(anna, new BigDecimal("450.00"))
                ));

        List<DebtResponse> result = debtCalculatorService.calculate(List.of(expense));

        assertEquals(1, result.size());
        assertEquals("Anna", result.get(0).getFromUser());
        assertEquals("Konrad", result.get(0).getToUser());
        assertEquals(new BigDecimal("450.00"), result.get(0).getAmount());
    }

    @Test
    void shouldCalculateDebtWithThreePeople() {
        Expense expense = createExpense(konrad, new BigDecimal("300.00"),
                List.of(
                        createSplit(konrad, new BigDecimal("100.00")),
                        createSplit(anna, new BigDecimal("100.00")),
                        createSplit(piotr, new BigDecimal("100.00"))
                ));

        List<DebtResponse> result = debtCalculatorService.calculate(List.of(expense));

        assertEquals(2, result.size());
        assertEquals(new BigDecimal("200.00"),
                result.stream()
                        .map(DebtResponse::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void shouldReturnEmptyListWhenExpensesAreSettled() {
        Expense expense1 = createExpense(konrad, new BigDecimal("100.00"),
                List.of(
                        createSplit(konrad, new BigDecimal("50.00")),
                        createSplit(anna, new BigDecimal("50.00"))
                ));

        Expense expense2 = createExpense(anna, new BigDecimal("100.00"),
                List.of(
                        createSplit(konrad, new BigDecimal("50.00")),
                        createSplit(anna, new BigDecimal("50.00"))
                ));

        List<DebtResponse> result = debtCalculatorService.calculate(List.of(expense1, expense2));

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldMinimizeNumberOfTransactions() {
        Expense expense1 = createExpense(konrad, new BigDecimal("100.00"),
                List.of(
                        createSplit(konrad, new BigDecimal("50.00")),
                        createSplit(anna, new BigDecimal("50.00"))
                ));

        Expense expense2 = createExpense(konrad, new BigDecimal("60.00"),
                List.of(
                        createSplit(konrad, new BigDecimal("30.00")),
                        createSplit(piotr, new BigDecimal("30.00"))
                ));

        List<DebtResponse> result = debtCalculatorService.calculate(List.of(expense1, expense2));

        assertEquals(2, result.size());
    }

    private Expense createExpense(User payer, BigDecimal amount, List<ExpenseSplit> splits) {
        Expense expense = Expense.builder()
                .payer(payer)
                .amount(amount)
                .description("Test")
                .splits(splits)
                .build();
        splits.forEach(split -> split.setExpense(expense));
        return expense;
    }

    private ExpenseSplit createSplit(User user, BigDecimal amount) {
        return ExpenseSplit.builder()
                .user(user)
                .amountOwed(amount)
                .build();
    }
}