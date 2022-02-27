package ru.gb.springbootmarket.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gb.springbootmarket.model.Category;
import ru.gb.springbootmarket.model.Product;
import ru.gb.springbootmarket.service.ProductService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductRestControllerTest {

    private ProductRestController productRestController;
    private Product product = new Product();

    @BeforeEach
    void setUp() {
        ProductService productService = mock(ProductService.class);
        when((productService.findById(3L))).thenReturn(Optional.ofNullable(product));

        productRestController = new ProductRestController();
        productRestController.setProductService(productService);
    }

    @Test
    void getProductById() {
        Product currentProduct = productRestController.getProductById(3L).orElse(null);
        Category category = new Category();
        category.setId(2L);
        category.setTitle("Бытовая техника");
        product.setId(3L);
        product.setTitle("Стиральная машинка LG");
        product.setPrice(32290f);
        product.setCategory(category);
        assertSame(currentProduct, product);
    }
}