package ru.gb.springbootmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.gb.springbootmarket.enums.OrderStatus;
import ru.gb.springbootmarket.enums.StorageStatus;
import ru.gb.springbootmarket.model.Order;
import ru.gb.springbootmarket.model.OrderItem;
import ru.gb.springbootmarket.service.OrderItemService;
import ru.gb.springbootmarket.service.OrderService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/management")
public class ManagerController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public ManagerController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public String getAllOrders(Model model) {
        List<Order> orders = orderService.getAllActive();
        model.addAttribute("orders", orders);
        return "management/index";
    }

    @GetMapping("/change/{id}")
    public String changeOrderStatus(@PathVariable Long id, Model model) {
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.values());;
        model.addAttribute("statuses", statuses);
        model.addAttribute("order", orderService.getOrderById(id));
        return "management/change_order_status_form";
    }

    @PostMapping("/change/{id}")
    @Transactional
    public String changeStatus(@PathVariable Long id,
                               @Valid Order order,
                               BindingResult bindingResult,
                               Principal principal) {
        if (bindingResult.hasErrors()) {
            return "management/change_order_status_form";
        }
        Order order1 = orderService.getOrderById(id);
        order1.setManager(principal.getName());
        order1.setOrderStatus(order.getOrderStatus());
        orderService.save(order1);
        return "redirect:/management";
    }

    @GetMapping("/items_by_id/{id}")
    public String getAllOrderItemsByOrderId(@PathVariable Long id, Model model, Principal principal) {
        Order order = orderService.getOrderById(id);
        if (order.getOrderStatus() == OrderStatus.NEW) order.setOrderStatus(OrderStatus.IN_WORK);
        order.setManager(principal.getName());
        orderService.save(order);
        List<OrderItem> orderItems = orderService.getOrderById(id).getOrderItems();
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("order", order);
        return "management/order_items";
    }

    @GetMapping("/item/{id}")
    public String changeOrderItemStatus(@PathVariable Long id, Model model) {
        List<StorageStatus> statuses = Arrays.asList(StorageStatus.values());
        model.addAttribute("statuses", statuses);
        model.addAttribute("orderItem", orderItemService.getOrderItemById(id));
        return "management/change_orderitem_status_form";
    }

    @PostMapping("/item/{id}")
    @Transactional
    public String changeStatus(@PathVariable Long id, OrderItem orderItem, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "management/change_orderitem_status_form";
        }
        OrderItem orderItem1 = orderItemService.getOrderItemById(id);
        orderItem1.setStorageStatus(orderItem.getStorageStatus());
        orderItemService.save(orderItem1);
        List<OrderItem> orderItems = orderService.getOrderById(orderItem1.getOrder().getId()).getOrderItems();
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("order", orderItem1.getOrder());
        if (orderItems.stream().noneMatch(orderItem2 ->
                orderItem2.getStorageStatus().getName().equals(StorageStatus.IN_SELECTION.getName()))) {
            Order order = orderService.getOrderById(orderItem1.getOrder().getId());
            order.setOrderStatus(OrderStatus.SHIPPED);
            orderService.save(order);
            return "redirect:/management";
        }
        return "management/order_items";
    }

    @PostMapping("/complete/{id}")
    public String orderComplete(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        order.setIs_active(false);
        orderService.save(order);
        return "redirect:/management";
    }
}
