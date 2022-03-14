package ru.gb.springbootmarket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.springbootmarket.dto.Cart;
import ru.gb.springbootmarket.enums.OrderStatus;
import ru.gb.springbootmarket.enums.ShippingMethod;
import ru.gb.springbootmarket.enums.StorageStatus;
import ru.gb.springbootmarket.model.Customer;
import ru.gb.springbootmarket.model.MarketUser;
import ru.gb.springbootmarket.model.Order;
import ru.gb.springbootmarket.model.OrderItem;
import ru.gb.springbootmarket.repository.OrderRepository;
import ru.gb.springbootmarket.repository.ProductRepository;
import ru.gb.springbootmarket.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.gb.springbootmarket.enums.EmailType.MANAGER_ORDER_CREATED;
import static ru.gb.springbootmarket.enums.EmailType.USER_ORDER_CREATED;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository,
                        CartService cartService,
                        UserRepository userRepository,
                        ProductRepository productRepository,
                        EmailService emailService,
                        UserService userService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Transactional
    public Order placeOrder(String address, String email, HttpServletRequest request) {
        Cart cart = cartService.getCartForCurrentUser(request);
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
        order.setDeliverTime(LocalDateTime.now().plusDays(1L));

        return getOrder(cart, order, request);
    }

    @Transactional
    public Order placeOrder(Principal principal, HttpServletRequest request) {
        Cart cart = cartService.getCartForCurrentUser(request);
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
        order.setDeliverTime(LocalDateTime.now().plusDays(1L));

        return getOrder(cart, order, request);
    }

    private Order getOrder(Cart cart, Order order, HttpServletRequest request) {
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setQuantity(cartItem.getCount());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setProduct(productRepository.getById(cartItem.getProductId()));
                    orderItem.setStorageStatus(StorageStatus.IN_SELECTION);
                    return orderItem;
                }).collect(Collectors.toList());
        order.setOrderItems(orderItems);
        orderRepository.save(order);
        cartService.removeCartForCurrentUser(request);

        List<String> managerEmails = userService.getActiveManagers().stream().map(MarketUser::getCustomer).map(Customer::getEmail).collect(Collectors.toList());
        emailService.sendMail(USER_ORDER_CREATED, Map.of("orderId", order.getId(), "price", order.getPrice()), List.of(order.getContactEmail()));
        emailService.sendMail(MANAGER_ORDER_CREATED, Map.of("orderId", order.getId()), managerEmails);

        return order;
    }

    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    public List<Order> getAllActive() { return orderRepository.getAllActive();}

    public List<Order> getOrdersByCustomer(Long id) {
        return orderRepository.getOrdersByCustomer(id);
    }

    public Order getOrderById(Long id) {
        return orderRepository.getById(id);
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public void delete(Order order) {orderRepository.delete(order);}
}
