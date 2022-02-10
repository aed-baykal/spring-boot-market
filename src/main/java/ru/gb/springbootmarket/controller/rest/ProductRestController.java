package ru.gb.springbootmarket.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.springbootmarket.model.Product;
import ru.gb.springbootmarket.service.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest")
public class ProductRestController {

    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAll();
    }

    @PostMapping
    public void addProduct(@RequestBody Product product) {
        product.setId(0L);
        productService.save(product);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void updateProduct(@RequestBody Product product) {
        productService.save(product);
    }

    @DeleteMapping("/{id}")
    public int deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return HttpStatus.OK.value();
    }

}
