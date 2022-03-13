package ru.gb.springbootmarket.controller;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.gb.springbootmarket.api.products.GetProductResponse;
import ru.gb.springbootmarket.api.products.Item;
import ru.gb.springbootmarket.model.Product;
import ru.gb.springbootmarket.service.ProductService;

import java.util.List;

@Endpoint
public class ProductsEndpoint {

    private final ProductService productService;

    public ProductsEndpoint(ProductService productService) {
        this.productService = productService;
    }

    @PayloadRoot(namespace = "http://gb.ru/springbootmarket/api/products", localPart = "getProductsRequest")
    @ResponsePayload
    public GetProductResponse getProducts() {
        GetProductResponse response = new GetProductResponse();
        List<Item> result = response.getResult();
        List<Product> products = productService.getAll();
        for (Product product : products) {
            result.add(productToItem(product));
        }
        return response;
    }

    public static Item productToItem(Product product) {
        if (product == null) {
            return null;
        }
        Item item = new Item();
        item.setId(product.getId());
        item.setTitle(product.getTitle());
        if (product.getPrice() != null) item.setPrice(product.getPrice());

        return item;
    }

}
