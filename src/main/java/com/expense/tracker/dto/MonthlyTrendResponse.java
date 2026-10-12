package com.expense.tracker.dto;

import java.math.BigDecimal;

public class MonthlyTrendResponse {
    private String month;
    private BigDecimal total;

    public MonthlyTrendResponse() {
    }

    public MonthlyTrendResponse(String month, BigDecimal total) {
        this.month = month;
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
