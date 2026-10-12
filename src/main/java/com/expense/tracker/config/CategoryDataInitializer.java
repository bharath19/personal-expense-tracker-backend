package com.expense.tracker.config;

import com.expense.tracker.entity.Category;
import com.expense.tracker.repository.CategoryRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryDataInitializer implements ApplicationRunner {
    private static final List<CategorySeed> DEFAULT_CATEGORIES = List.of(
        new CategorySeed("Food & Dining", "Meals, groceries, and dining out"),
        new CategorySeed("Transportation", "Fuel, transit, and rides"),
        new CategorySeed("Housing", "Rent, mortgage, and home expenses"),
        new CategorySeed("Utilities", "Electricity, water, internet, and phone"),
        new CategorySeed("Healthcare", "Medical care and prescriptions"),
        new CategorySeed("Shopping", "Clothing and personal purchases"),
        new CategorySeed("Entertainment", "Events, subscriptions, and hobbies"),
        new CategorySeed("Education", "Courses, books, and school expenses"),
        new CategorySeed("Other", "Expenses that do not fit another category")
    );

    private final CategoryRepository categoryRepository;

    public CategoryDataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (CategorySeed seed : DEFAULT_CATEGORIES) {
            if (!categoryRepository.existsByName(seed.name())) {
                categoryRepository.save(new Category(seed.name(), seed.description()));
            }
        }
    }

    private record CategorySeed(String name, String description) {
    }
}
