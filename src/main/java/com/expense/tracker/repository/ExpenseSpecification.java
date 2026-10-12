package com.expense.tracker.repository;

import com.expense.tracker.entity.Expense;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseSpecification {

    public static Specification<Expense> filterExpenses(Long userId,
                                                       Long categoryId,
                                                       String keyword,
                                                       String paymentMethod,
                                                       LocalDate fromDate,
                                                       LocalDate toDate,
                                                       BigDecimal minAmount,
                                                       BigDecimal maxAmount) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }
            if (keyword != null && !keyword.isBlank()) {
                String value = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), value),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("notes")), value),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("paymentMethod")), value),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("name")), value)
                ));
            }
            if (paymentMethod != null && !paymentMethod.isBlank()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("paymentMethod")), paymentMethod.trim().toLowerCase()));
            }
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), fromDate));
            }
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), toDate));
            }
            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
