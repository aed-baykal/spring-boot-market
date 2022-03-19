package ru.gb.springbootmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.gb.springbootmarket.converter.ProductMapper;
import ru.gb.springbootmarket.dto.ProductDto;
import ru.gb.springbootmarket.dto.ProductShortDto;
import ru.gb.springbootmarket.model.Product;
import ru.gb.springbootmarket.service.CategoryService;
import ru.gb.springbootmarket.service.ProductElasticSearchService;
import ru.gb.springbootmarket.service.ProductService;
import ru.gb.springbootmarket.service.StorageService;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;
    private final StorageService storageService;
    private final ProductElasticSearchService productElasticSearchService;

    public AdminController(ProductService productService,
                           CategoryService categoryService,
                           ProductMapper productMapper,
                           StorageService storageService,
                           ProductElasticSearchService productElasticSearchService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
        this.storageService = storageService;
        this.productElasticSearchService = productElasticSearchService;
    }

    @GetMapping
    public String getAllProducts(Model model) {
        List<ProductDto> productDtos =  productService.getAll().stream()
                .map(productMapper::productToProductDto).collect(Collectors.toList());
        model.addAttribute("products", productDtos);
        return "admin/index";
    }

    @GetMapping("/add")
    public String getProductAddForm(Model model) {
        model.addAttribute("productShortDto", new ProductShortDto());
        model.addAttribute("categories", categoryService.getAllTitles());
        return "admin/add_product_form";
    }

    @PostMapping("/add")
    @Transactional
    public String saveProduct(@Valid ProductShortDto productShortDto,
                              @RequestParam MultipartFile image,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "admin/add_product_form";
        }
        try {
            storageService.store(image);
            productShortDto.setImageUrl("/media/" + image.getOriginalFilename());
            productService.save(productMapper.productShortDtoToProduct(productShortDto));
            productElasticSearchService.indexProduct(productMapper.productToProductDto(productMapper.productShortDtoToProduct(productShortDto)));
        } catch (RuntimeException | IOException ex) {
            model.addAttribute("notFound", ex);
            return "admin/add_product_form";
        }
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteProduct(@PathVariable Long id) {
        Product product = productService.findById(id).orElse(null);
        if ((product != null) && (product.getImageUrl() != null)) {
            List<Product> productList = productService.getAllByImageUrl(product.getImageUrl());
            Path location = Paths.get(product.getImageUrl());
            if (productList.size() == 1) storageService.delete(location);
        }
        productService.deleteById(id);
        List<ProductDto> productDtos =  productService.getAll().stream()
                .map(productMapper::productToProductDto).collect(Collectors.toList());
        productDtos.forEach(productDto -> {
            try {
                productElasticSearchService.indexProduct(productDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return "redirect:/admin";
    }
}
