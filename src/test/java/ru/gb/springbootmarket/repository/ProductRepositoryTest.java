package ru.gb.springbootmarket.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.gb.springbootmarket.model.Category;
import ru.gb.springbootmarket.model.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void findAllTest() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setTitle("Электроника");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setTitle("Бытовая техника");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setTitle("Ноутбук Lenovo");
        product1.setPrice(44990f);
        product1.setCategory(category1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setTitle("Телефон iPhone");
        product2.setPrice(66490f);
        product2.setCategory(category1);

        Product product3 = new Product();
        product3.setId(3L);
        product3.setTitle("Стиральная машинка LG");
        product3.setPrice(32290f);
        product3.setCategory(category2);

        entityManager.merge(product1);
        entityManager.merge(product2);
        entityManager.merge(product3);

        List<Product> products = productRepository.findAll();
        assertTrue(products.stream().allMatch(product -> product.getCategory() != null));
        assertEquals(products.get(0).getCategory().getTitle(), "Смартфоны и гаджеты");
    }
}