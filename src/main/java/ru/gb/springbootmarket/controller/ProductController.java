package ru.gb.springbootmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gb.springbootmarket.converter.ProductMapper;
import ru.gb.springbootmarket.dto.ProductDto;
import ru.gb.springbootmarket.model.Baner;
import ru.gb.springbootmarket.service.BanerService;
import ru.gb.springbootmarket.service.ProductElasticSearchService;
import ru.gb.springbootmarket.service.ProductService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ProductElasticSearchService productElasticSearchService;
    private final BanerService banerService;
    private final static String BANER_PLUG = "/images/banner_plug.png";

    public ProductController(ProductService productService,
                             ProductMapper productMapper,
                             ProductElasticSearchService productElasticSearchService,
                             BanerService banerService) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.productElasticSearchService = productElasticSearchService;
        this.banerService = banerService;
    }

    @GetMapping
    public String getAllProducts(Model model) {
        List<ProductDto> productDtos =  productService.getAll().stream()
                .map(productMapper::productToProductDto).collect(Collectors.toList());
        pagePreparation(model, productDtos);
        return "home";
    }

    private void pagePreparation(Model model, List<ProductDto> productDtos) {
        Baner banerActive = new Baner();
        List<Baner> baners = banerService.getAll();
        if (baners.size() > 0) banerActive = baners.get(0);
        if(baners.size() > 1) baners.remove(banerActive);
        if (baners.size() == 0) {
            banerActive.setImageUrl(BANER_PLUG);
            baners.add(banerActive);
        }
        model.addAttribute("products", productDtos);
        model.addAttribute("baner_active", banerActive);
        model.addAttribute("baners", baners);
    }

    @GetMapping("/info/{id}")
    public String getProductInfo(@PathVariable Long id, Model model) {
        model.addAttribute("product", productMapper.productToProductDto(productService.findById(id).orElse(null)));
        return "product_info";
    }

    @GetMapping("/search")
    public String getSearchResult(@RequestParam String search, Model model) throws IOException {
        List<ProductDto> productDtos = productElasticSearchService.search(search);
        pagePreparation(model, productDtos);
        return "home";
    }
}
