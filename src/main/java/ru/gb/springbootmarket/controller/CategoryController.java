package ru.gb.springbootmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.gb.springbootmarket.model.Category;
import ru.gb.springbootmarket.repository.CategoryRepository;

@Controller
@RequestMapping("/category")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String getAllCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "category/categories";
    }

    @GetMapping
    @RequestMapping("/{id}")
    public String getProductsByCategoryId(@PathVariable Long id, Model model) {
        Category category = categoryRepository.findByIdFetchProducts(id);
        model.addAttribute("title", category.getTitle());
        model.addAttribute("products", category.getProducts());
        return "category/products_of_category";
    }
}
