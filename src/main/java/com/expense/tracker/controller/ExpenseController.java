package com.expense.tracker.controller;

import com.expense.tracker.dto.*;
import com.expense.tracker.entity.User;
import com.expense.tracker.security.UserPrincipal;
import com.expense.tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/expenses")
    public ResponseEntity<ExpenseResponse> createExpense(@AuthenticationPrincipal UserPrincipal principal,
                                                       @Valid @RequestBody ExpenseRequest request) {
        User currentUser = principal.getUser();
        return new ResponseEntity<>(expenseService.createExpense(currentUser, request), HttpStatus.CREATED);
    }

    @GetMapping("/expenses")
    public ResponseEntity<PagedModel<ExpenseResponse>> getExpenses(@AuthenticationPrincipal UserPrincipal principal,
                                                            @RequestParam(required = false) Long categoryId,
                                                            @RequestParam(required = false) String keyword,
                                                            @RequestParam(required = false) String paymentMethod,
                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                            @RequestParam(required = false) BigDecimal minAmount,
                                                            @RequestParam(required = false) BigDecimal maxAmount,
                                                            @RequestParam(defaultValue = "date") String sortBy,
                                                            @RequestParam(defaultValue = "desc") String direction,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        Page<ExpenseResponse> expenses = expenseService.getExpenses(
            principal.getUser(), categoryId, keyword, paymentMethod,
            fromDate, toDate, minAmount, maxAmount, sortBy, direction, page, size);
        return ResponseEntity.ok(new PagedModel<>(expenses));
        }

        @GetMapping("/expenses/suggestions")
        public ResponseEntity<List<String>> getExpenseSuggestions(@AuthenticationPrincipal UserPrincipal principal,
                                       @RequestParam String query) {
        return ResponseEntity.ok(expenseService.getExpenseSuggestions(principal.getUser(), query));
    }

    @GetMapping("/expenses/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@AuthenticationPrincipal UserPrincipal principal,
                                                        @PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(principal.getUser(), id));
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable Long id,
                                                       @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.updateExpense(principal.getUser(), id, request));
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@AuthenticationPrincipal UserPrincipal principal,
                                             @PathVariable Long id) {
        expenseService.deleteExpense(principal.getUser(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expenses/summary")
    public ResponseEntity<ExpenseSummaryResponse> getExpenseSummary(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(expenseService.getExpenseSummary(principal.getUser()));
    }

    @GetMapping("/expenses/category-summary")
    public ResponseEntity<List<CategorySummaryResponse>> getCategorySummary(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(expenseService.getCategorySummary(principal.getUser()));
    }

    @GetMapping("/expenses/monthly-trend")
    public ResponseEntity<List<MonthlyTrendResponse>> getMonthlyTrend(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(expenseService.getMonthlyTrend(principal.getUser()));
    }
}
