package ru.gb.springbootmarket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.springbootmarket.dto.Cart;
import ru.gb.springbootmarket.enums.OrderStatus;
import ru.gb.springbootmarket.enums.ShippingMethod;
import ru.gb.springbootmarket.model.Customer;
import ru.gb.springbootmarket.model.Order;
import ru.gb.springbootmarket.model.OrderItem;
import ru.gb.springbootmarket.repository.OrderRepository;
import ru.gb.springbootmarket.repository.ProductRepository;
import ru.gb.springbootmarket.repository.UserRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CartService cartService,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order placeOrder(String address, String email) {
        Cart cart = cartService.getCartForCurrentUser();
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }
        Order order = new Order();
                order.setAddress(address);
                order.setContactEmail(email);
        order.setPrice(cart.getPrice());
        order.setOrderStatus(OrderStatus.NEW);
        order.setShippingMethod(ShippingMethod.DELIVERY);
        order.setCreationTime(LocalDateTime.now());

        return getOrder(cart, order);
    }

    @Transactional
    public Order placeOrder(Principal principal) {
        Cart cart = cartService.getCartForCurrentUser();
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }
        Order order = new Order();
        Customer customer = Objects.requireNonNull(userRepository.findByLogin(principal.getName()).orElse(null)).getCustomer();
        order.setCustomer(customer);
        order.setAddress(customer.getAddress());
        order.setContactEmail(customer.getEmail());
        order.setPrice(cart.getPrice());
        order.setOrderStatus(OrderStatus.NEW);
        order.setShippingMethod(ShippingMethod.DELIVERY);
        order.setCreationTime(LocalDateTime.now());

        return getOrder(cart, order);
    }

    private Order getOrder(Cart cart, Order order) {
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setQuantity(cartItem.getCount());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setProduct(productRepository.getById(cartItem.getProductId()));
                    return orderItem;
                }).collect(Collectors.toList());
        order.setOrderItems(orderItems);
        orderRepository.save(order);
        cartService.init();
        return order;
    }

}
