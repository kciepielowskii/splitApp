package com.splitapp.splitApp.controller;

import com.splitapp.splitApp.dto.request.CreateExpenseRequest;
import com.splitapp.splitApp.dto.request.CreateGroupRequest;
import com.splitapp.splitApp.dto.response.DebtResponse;
import com.splitapp.splitApp.dto.response.ExpenseResponse;
import com.splitapp.splitApp.dto.response.GroupResponse;
import com.splitapp.splitApp.model.User;
import com.splitapp.splitApp.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(groupService.createGroup(request, user));
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getUserGroups(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(groupService.getUserGroups(user));
    }

    @PostMapping("/expenses")
    public ResponseEntity<ExpenseResponse> addExpense(
            @Valid @RequestBody CreateExpenseRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(groupService.addExpense(request, user));
    }

    @GetMapping("/{groupId}/expenses")
    public ResponseEntity<List<ExpenseResponse>> getGroupExpenses(
            @PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupExpenses(groupId));
    }

    @GetMapping("/{groupId}/balances")
    public ResponseEntity<List<DebtResponse>> getGroupBalances(
            @PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupBalances(groupId));
    }
}