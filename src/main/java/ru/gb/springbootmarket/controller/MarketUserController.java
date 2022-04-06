package ru.gb.springbootmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.gb.springbootmarket.model.Order;
import ru.gb.springbootmarket.model.OrderItem;
import ru.gb.springbootmarket.service.OrderService;
import ru.gb.springbootmarket.service.UserService;

import java.util.List;

@Controller

@RequestMapping("/market_user")
public class MarketUserController {

    private final OrderService orderService;
    private final UserService userService;

    public MarketUserController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String getAllOrders(@PathVariable Long id,  Model model) {
        List<Order> orders = orderService.getOrdersByCustomer(id);
        model.addAttribute("orders", orders);
        return "market_user/index";
    }

    @GetMapping("/items_by_id/{id}")
    public String getAllOrderItemsByOrderId(@PathVariable Long id, Model model) {
        List<OrderItem> orderItems = orderService.getOrderById(id).getOrderItems();
        model.addAttribute("orderItems", orderItems);
        return "market_user/order_items";
    }

    @GetMapping("/user/{userName}")
    public String findUserByUsername(@PathVariable String userName, Model model) {
        model.addAttribute("user", userService.findUserByUserName(userName));
        return "market_user/user_info";
    }

}
