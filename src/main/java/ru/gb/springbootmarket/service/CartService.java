package ru.gb.springbootmarket.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import ru.gb.springbootmarket.converter.ProductMapper;
import ru.gb.springbootmarket.dto.Cart;
import ru.gb.springbootmarket.repository.CartRepository;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.UUID;

@Service
public class CartService {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final CartRepository cartRepository;

    public CartService(ProductService productService, ProductMapper productMapper, CartRepository cartRepository) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.cartRepository = cartRepository;
    }

    public Cart getCartForCurrentUser(HttpServletRequest request) {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        Cookie[] cookies = request.getCookies();
        String attributeUser = "";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user-id")) attributeUser = cookie.getValue();
            }
        }
        return getCart(principal, attributeUser);
    }

    public void removeCartForCurrentUser(HttpServletRequest request){
        cartRepository.delete(getCartForCurrentUser(request));
    }

    private Cart getCart(Principal principal, String attributeUser) {
        if(principal instanceof AnonymousAuthenticationToken) {
            if (attributeUser.equals("")) attributeUser = RequestContextHolder.currentRequestAttributes().getSessionId();
            return cartRepository.findById(attributeUser).orElse(new Cart(attributeUser));
        }
        return cartRepository.findById(principal.getName()).orElse(new Cart(principal.getName()));
    }

    public Cart addProductById(Long id, HttpServletRequest request) {
        Cart cart = getCartForCurrentUser(request);
        productService.findById(id).ifPresent(product -> cart.addItem(productMapper.productToCartItem(product)));
        cartRepository.save(cart);
        return cart;
    }

    public Cart removeProductById(Long id, HttpServletRequest request) {
        Cart cart = getCartForCurrentUser(request);
        cart.removeItem(id);
        cartRepository.save(cart);
        return cart;
    }

    public void setCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        boolean hasNotCookie = true;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user-id")) hasNotCookie = false;
            }
            if (hasNotCookie) {
                Cookie tokenCookie = new Cookie("user-id", UUID.randomUUID().toString());
                tokenCookie.setMaxAge(86400);
                tokenCookie.setSecure(true);
                tokenCookie.setHttpOnly(true);
                response.addCookie(tokenCookie);
            }
        }
    }

}
