package com.splitapp.splitApp.service;

import com.splitapp.splitApp.dto.response.DebtResponse;
import com.splitapp.splitApp.model.Expense;
import com.splitapp.splitApp.model.ExpenseSplit;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DebtCalculatorService {

    public List<DebtResponse> calculate(List<Expense> expenses) {
        Map<String, BigDecimal> balances = new HashMap<>();

        for (Expense expense : expenses) {
            String payer = expense.getPayer().getDisplayName();
            balances.merge(payer, expense.getAmount(), BigDecimal::add);

            for (ExpenseSplit split : expense.getSplits()) {
                String debtor = split.getUser().getDisplayName();
                balances.merge(debtor, split.getAmountOwed().negate(), BigDecimal::add);
            }
        }

        List<Map.Entry<String, BigDecimal>> creditors = new ArrayList<>();
        List<Map.Entry<String, BigDecimal>> debtors = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : balances.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(entry);
            } else if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(entry);
            }
        }

        creditors.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        debtors.sort((a, b) -> a.getValue().compareTo(b.getValue()));

        List<DebtResponse> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < creditors.size() && j < debtors.size()) {
            Map.Entry<String, BigDecimal> creditor = creditors.get(i);
            Map.Entry<String, BigDecimal> debtor = debtors.get(j);

            BigDecimal creditorAmount = creditor.getValue();
            BigDecimal debtorAmount = debtor.getValue().negate();
            BigDecimal transferAmount = creditorAmount.min(debtorAmount);

            result.add(new DebtResponse(
                    debtor.getKey(),
                    creditor.getKey(),
                    transferAmount
            ));

            creditor.setValue(creditorAmount.subtract(transferAmount));
            debtor.setValue(debtor.getValue().add(transferAmount));

            if (creditor.getValue().compareTo(BigDecimal.ZERO) == 0) i++;
            if (debtor.getValue().compareTo(BigDecimal.ZERO) == 0) j++;
        }

        return result;
    }
}