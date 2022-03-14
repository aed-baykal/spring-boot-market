package ru.gb.springbootmarket.controller.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.springbootmarket.dto.Cart;
import ru.gb.springbootmarket.service.CartService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/cart")
public class CartRestController {

    private final CartService cartService;

    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping// GET cart
    public Cart getCart(HttpServletRequest request, HttpServletResponse response) {
        cartService.setCookie(request, response);
        return cartService.getCartForCurrentUser(request);
    }

    @PostMapping("/product/{id}") // POST cart/product/1
    public Cart addProduct(@PathVariable Long id, HttpServletRequest request) {
        return cartService.addProductById(id, request);
    }

    @DeleteMapping("/product/{id}") // DELETE cart/product/1
    public Cart deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        return cartService.removeProductById(id, request);
    }
}
