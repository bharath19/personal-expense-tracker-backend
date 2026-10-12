package com.expense.tracker.dto;

import java.math.BigDecimal;

public class ExpenseSummaryResponse {
    private BigDecimal totalExpenses;
    private BigDecimal todayExpenses;
    private BigDecimal monthlyBudget;
    private BigDecimal remainingBudget;
    private BigDecimal currentMonthExpenses;

    public ExpenseSummaryResponse() {
    }

    public ExpenseSummaryResponse(BigDecimal totalExpenses,
                                 BigDecimal todayExpenses,
                                 BigDecimal monthlyBudget,
                                 BigDecimal remainingBudget,
                                 BigDecimal currentMonthExpenses) {
        this.totalExpenses = totalExpenses;
        this.todayExpenses = todayExpenses;
        this.monthlyBudget = monthlyBudget;
        this.remainingBudget = remainingBudget;
        this.currentMonthExpenses = currentMonthExpenses;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getTodayExpenses() {
        return todayExpenses;
    }

    public void setTodayExpenses(BigDecimal todayExpenses) {
        this.todayExpenses = todayExpenses;
    }

    public BigDecimal getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(BigDecimal monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public BigDecimal getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(BigDecimal remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public BigDecimal getCurrentMonthExpenses() {
        return currentMonthExpenses;
    }

    public void setCurrentMonthExpenses(BigDecimal currentMonthExpenses) {
        this.currentMonthExpenses = currentMonthExpenses;
    }
}
