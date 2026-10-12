package com.expense.tracker.repository;

import com.expense.tracker.entity.Expense;
import com.expense.tracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
    @Override
    @EntityGraph(attributePaths = "category")
    Page<Expense> findAll(Specification<Expense> specification, Pageable pageable);

    Page<Expense> findByUser(User user, Pageable pageable);
    List<Expense> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    List<Expense> findByUserAndDate(User user, LocalDate date);
    List<Expense> findByUser(User user);

    @Query("select distinct e.description from Expense e where e.user = :user and lower(e.description) like lower(concat('%', :query, '%')) order by e.description")
    List<String> findDescriptionSuggestions(@Param("user") User user,
                                            @Param("query") String query,
                                            Pageable pageable);

    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.user = :user")
    BigDecimal sumAmountByUser(@Param("user") User user);

    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.user = :user and e.date between :startDate and :endDate")
    BigDecimal sumAmountByUserAndDateBetween(@Param("user") User user,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}
