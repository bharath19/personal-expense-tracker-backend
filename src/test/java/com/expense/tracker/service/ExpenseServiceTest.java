package com.expense.tracker.service;

import com.expense.tracker.dto.ExpenseRequest;
import com.expense.tracker.dto.ExpenseResponse;
import com.expense.tracker.entity.Category;
import com.expense.tracker.entity.Role;
import com.expense.tracker.entity.User;
import com.expense.tracker.repository.ExpenseRepository;
import com.expense.tracker.repository.CategoryRepository;
import com.expense.tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ExpenseServiceTest {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void createExpenseShouldPersistExpenseForUser() {
        expenseRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setName("Bob");
        user.setEmail("bob@example.com");
        user.setPassword("secret123");
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);

        Category category = new Category();
        category.setName("Food");
        category.setDescription("Meals and dining");
        Category savedCategory = categoryRepository.save(category);

        ExpenseRequest request = new ExpenseRequest();
        request.setDescription("Office lunch");
        request.setAmount(BigDecimal.valueOf(250.5));
        request.setDate(LocalDate.now());
        request.setCategoryId(savedCategory.getId());
        request.setPaymentMethod("UPI");
        request.setNotes("Team lunch");

        ExpenseResponse response = expenseService.createExpense(savedUser, request);

        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualByComparingTo("250.50");
        assertThat(response.getCategoryName()).isEqualTo("Food");
        assertThat(response.getDescription()).isEqualTo("Office lunch");
        assertThat(expenseService.getExpenseSuggestions(savedUser, "OFFI"))
            .containsExactly("Office lunch");
        assertThat(expenseService.getExpenses(savedUser, null, "food", null,
            null, null, null, null, "date", "desc", 0, 10).getContent())
            .hasSize(1);
        assertThat(expenseService.getExpenses(savedUser, null, "team lunch", null,
            null, null, null, null, "date", "desc", 0, 10).getContent())
            .hasSize(1);
        assertThat(expenseService.getExpenses(savedUser, null, "upi", null,
            null, null, null, null, "date", "desc", 0, 10).getContent())
            .hasSize(1);
    }
}
