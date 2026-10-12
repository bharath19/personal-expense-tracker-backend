package com.expense.tracker.dto;

import java.math.BigDecimal;

public class CategorySummaryResponse {
    private String categoryName;
    private BigDecimal total;

    public CategorySummaryResponse() {
    }

    public CategorySummaryResponse(String categoryName, BigDecimal total) {
        this.categoryName = categoryName;
        this.total = total;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
