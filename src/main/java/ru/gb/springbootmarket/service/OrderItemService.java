package ru.gb.springbootmarket.service;

import org.springframework.stereotype.Service;
import ru.gb.springbootmarket.model.OrderItem;
import ru.gb.springbootmarket.repository.OrderItemRepository;

@Service
public class OrderItemService {
    private OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public OrderItem getOrderItemById(Long id) {return orderItemRepository.getById(id);}

    public void save(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }

    public void delete(OrderItem orderItem) {orderItemRepository.delete(orderItem);}
}
