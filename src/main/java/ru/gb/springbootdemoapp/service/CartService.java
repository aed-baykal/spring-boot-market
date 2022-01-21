package ru.gb.springbootdemoapp.service;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import ru.gb.springbootdemoapp.converter.ProductMapper;
import ru.gb.springbootdemoapp.dto.Cart;

@Service
public class CartService {

  private ProductService productService;
  private ProductMapper productMapper;
  private Cart cart;

  public CartService(ProductService productService, ProductMapper productMapper) {
    this.productService = productService;
    this.productMapper = productMapper;
  }

  @PostConstruct
  public void init() {
    cart = new Cart();
  }

  public Cart getCartForCurrnetUser() {
    return cart;
  }

  public Cart addProductById(Long id) {
    productService.findById(id).ifPresent(product -> cart.addItem(productMapper.productToCartItem(product)));
    return cart;
  }

  public Cart removeProductById(Long id) {
    cart.removeItem(id);
    return cart;
  }
}
