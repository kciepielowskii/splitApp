package com.splitapp.splitApp.service;

import com.splitapp.splitApp.dto.request.CreateExpenseRequest;
import com.splitapp.splitApp.dto.request.CreateGroupRequest;
import com.splitapp.splitApp.dto.response.DebtResponse;
import com.splitapp.splitApp.dto.response.ExpenseResponse;
import com.splitapp.splitApp.dto.response.GroupResponse;
import com.splitapp.splitApp.exception.ResourceNotFoundException;
import com.splitapp.splitApp.model.Expense;
import com.splitapp.splitApp.model.ExpenseSplit;
import com.splitapp.splitApp.model.Group;
import com.splitapp.splitApp.model.User;
import com.splitapp.splitApp.repository.ExpenseRepository;
import com.splitapp.splitApp.repository.GroupRepository;
import com.splitapp.splitApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final DebtCalculatorService debtCalculatorService;

    public GroupResponse createGroup(CreateGroupRequest request, User owner) {
        Group group = Group.builder()
                .name(request.getName())
                .owner(owner)
                .members(new ArrayList<>())
                .build();

        group.getMembers().add(owner);

        if (request.getMemberIds() != null) {
            for (Long memberId : request.getMemberIds()) {
                userRepository.findById(memberId).ifPresent(group.getMembers()::add);
            }
        }

        groupRepository.save(group);
        return mapToResponse(group);
    }

    public List<GroupResponse> getUserGroups(User user) {
        return groupRepository.findByMembersId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ExpenseResponse addExpense(CreateExpenseRequest request, User payer) {
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Expense expense = Expense.builder()
                .group(group)
                .payer(payer)
                .amount(request.getAmount())
                .description(request.getDescription())
                .splits(new ArrayList<>())
                .build();

        List<User> splitUsers;
        if (request.getSplitBetweenUserIds() != null && !request.getSplitBetweenUserIds().isEmpty()) {
            splitUsers = userRepository.findAllById(request.getSplitBetweenUserIds());
        } else {
            splitUsers = group.getMembers();
        }

        BigDecimal splitAmount = request.getAmount()
                .divide(BigDecimal.valueOf(splitUsers.size()), 2, RoundingMode.HALF_UP);

        for (User user : splitUsers) {
            ExpenseSplit split = ExpenseSplit.builder()
                    .expense(expense)
                    .user(user)
                    .amountOwed(splitAmount)
                    .build();
            expense.getSplits().add(split);
        }

        expenseRepository.save(expense);
        return mapExpenseToResponse(expense);
    }

    public List<DebtResponse> getGroupBalances(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        return debtCalculatorService.calculate(expenses);
    }

    public List<ExpenseResponse> getGroupExpenses(Long groupId) {
        return expenseRepository.findByGroupId(groupId)
                .stream()
                .map(this::mapExpenseToResponse)
                .toList();
    }

    private GroupResponse mapToResponse(Group group) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getOwner().getDisplayName(),
                group.getMembers().stream().map(User::getDisplayName).toList(),
                group.getCreatedAt()
        );
    }

    private ExpenseResponse mapExpenseToResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getPayer().getDisplayName(),
                expense.getCreatedAt()
        );
    }
}