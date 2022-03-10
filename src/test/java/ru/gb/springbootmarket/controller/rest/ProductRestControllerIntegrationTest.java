package ru.gb.springbootmarket.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.gb.springbootmarket.model.Product;
import ru.gb.springbootmarket.service.ProductService;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ProductRestControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private Product product = new Product();
    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        product.setId(3L);
        product.setTitle("Стиральная машинка LG");
        product.setPrice(32290f);
    }

    @Test
    void getProductByIdTest() {
        ResponseEntity<Product> entity = restTemplate.getForEntity("/rest/3", Product.class);
        assertSame(entity.getStatusCode(), HttpStatus.OK);
//        assertEquals(entity.getBody().getPrice(), 32290f);
    }

    @Test
    void deleteProductTest() {
        restTemplate.delete("/rest/3", null, Product.class);
        verify(productService).deleteById(3L);
    }

}