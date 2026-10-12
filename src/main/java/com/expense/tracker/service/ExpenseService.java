package com.expense.tracker.service;

import com.expense.tracker.dto.*;
import com.expense.tracker.entity.Category;
import com.expense.tracker.entity.Expense;
import com.expense.tracker.entity.User;
import com.expense.tracker.exception.BadRequestException;
import com.expense.tracker.exception.ResourceNotFoundException;
import com.expense.tracker.repository.ExpenseRepository;
import com.expense.tracker.repository.ExpenseSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryService categoryService;

    public ExpenseService(ExpenseRepository expenseRepository, CategoryService categoryService) {
        this.expenseRepository = expenseRepository;
        this.categoryService = categoryService;
    }

    public ExpenseResponse createExpense(User user, ExpenseRequest request) {
        Category category = categoryService.getCategoryEntity(request.getCategoryId());
        Expense expense = new Expense(
                request.getAmount(),
                category,
                request.getDate(),
                request.getDescription(),
                request.getPaymentMethod(),
                request.getNotes(),
                user
        );
        Expense saved = expenseRepository.save(expense);
        return mapToResponse(saved);
    }

    public Page<ExpenseResponse> getExpenses(User currentUser,
                                            Long categoryId,
                                            String keyword,
                                            String paymentMethod,
                                            LocalDate fromDate,
                                            LocalDate toDate,
                                            BigDecimal minAmount,
                                            BigDecimal maxAmount,
                                            String sortBy,
                                            String direction,
                                            int page,
                                            int size) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy != null ? sortBy : "date").descending()
                : Sort.by(sortBy != null ? sortBy : "date").ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Expense> specification = ExpenseSpecification.filterExpenses(
                currentUser.getId(), categoryId, keyword, paymentMethod, fromDate, toDate, minAmount, maxAmount);

        return expenseRepository.findAll(specification, pageable).map(this::mapToResponse);
    }

    public List<String> getExpenseSuggestions(User currentUser, String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        return expenseRepository.findDescriptionSuggestions(
                currentUser, query.trim(), PageRequest.of(0, 8));
    }

    public ExpenseResponse getExpenseById(User user, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You are not allowed to access this expense");
        }
        return mapToResponse(expense);
    }

    public ExpenseResponse updateExpense(User user, Long expenseId, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You do not have permission to update this expense");
        }

        Category category = categoryService.getCategoryEntity(request.getCategoryId());
        expense.setAmount(request.getAmount());
        expense.setCategory(category);
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setNotes(request.getNotes());

        return mapToResponse(expenseRepository.save(expense));
    }

    public void deleteExpense(User user, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You do not have permission to delete this expense");
        }
        expenseRepository.delete(expense);
    }

    public ExpenseSummaryResponse getExpenseSummary(User user) {
        BigDecimal totalExpenses = expenseRepository.findByUser(user).stream()
            .map(expense -> expense.getAmount())
            .reduce(BigDecimal.ZERO, (total, amount) -> total.add(amount));

        LocalDate today = LocalDate.now();
        BigDecimal todayExpenses = expenseRepository.findByUserAndDate(user, today).stream()
            .map(expense -> expense.getAmount())
            .reduce(BigDecimal.ZERO, (total, amount) -> total.add(amount));

        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        BigDecimal currentMonthExpenses = expenseRepository.findByUserAndDateBetween(user, startOfMonth, endOfMonth).stream()
            .map(expense -> expense.getAmount())
            .reduce(BigDecimal.ZERO, (total, amount) -> total.add(amount));

        BigDecimal monthlyBudget = new BigDecimal("5000.00");
        BigDecimal remainingBudget = monthlyBudget.subtract(currentMonthExpenses);

        return new ExpenseSummaryResponse(totalExpenses, todayExpenses, monthlyBudget, remainingBudget, currentMonthExpenses);
    }

    public List<CategorySummaryResponse> getCategorySummary(User user) {
        List<Expense> expenses = expenseRepository.findByUser(user);
        Map<String, BigDecimal> totals = new LinkedHashMap<>();

        for (Expense expense : expenses) {
            String key = expense.getCategory().getName();
            totals.merge(key, expense.getAmount(), (total, amount) -> total.add(amount));
        }

        List<CategorySummaryResponse> results = new ArrayList<>();
        totals.forEach((name, total) -> results.add(new CategorySummaryResponse(name, total)));
        return results;
    }

    public List<MonthlyTrendResponse> getMonthlyTrend(User user) {
        List<MonthlyTrendResponse> trend = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            LocalDate start = month.atDay(1);
            LocalDate end = month.atEndOfMonth();
            BigDecimal total = expenseRepository.findByUserAndDateBetween(user, start, end).stream()
                    .map(expense -> expense.getAmount())
                    .reduce(BigDecimal.ZERO, (sum, amount) -> sum.add(amount));
            trend.add(new MonthlyTrendResponse(month.format(java.time.format.DateTimeFormatter.ofPattern("MMM yyyy")), total));
        }
        return trend;
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getCategory().getId(),
                expense.getCategory().getName(),
                expense.getDate(),
                expense.getDescription(),
                expense.getPaymentMethod(),
                expense.getNotes(),
                expense.getUser().getId(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}
